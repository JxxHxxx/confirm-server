package com.jxx.approval.confirm.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.approval.common.client.SimpleRestClient;
import com.jxx.approval.confirm.domain.document.ConfirmDocument;
import com.jxx.approval.confirm.domain.document.ConfirmDocumentException;
import com.jxx.approval.confirm.domain.document.ConfirmStatus;
import com.jxx.approval.confirm.domain.document.DocumentType;
import com.jxx.approval.confirm.dto.request.ConfirmStatusChangeRequest;
import com.jxx.approval.confirm.dto.response.ResponseResult;
import com.jxx.approval.vendor.vacation.ResourceIdExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmDocumentEventListener {
    /**
     * 현재 결재 문서가 결재자가 승인/반려를 할 수 있는 상태인지를 검증
     * 굳이 이 로직을 여기에 둘 필요가 있나 싶음...
     **/
    @TransactionalEventListener(value = ConfirmDocumentAcceptRejectEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(ConfirmDocumentAcceptRejectEvent event) {
        // 로그 작업
        ConfirmDocument confirmDocument = event.getConfirmDocument();
        if (!confirmDocument.validateConfirmStatusChange(event.getToConfirmStatus())) {
            log.info("\n {}", event.validateFailMessage());
            throw new ConfirmDocumentException("현재 결재 문서는 승인/반려할 수 없는 상태입니다.");
        } else {
            log.info("\n {}", event.validateSuccessMessage());
        }
    }

    /**
     * 결재 문서 최종 승인 시 발생하는 이벤트 처리 로직
     * 외부 통신이 발생할 수 있다. SYNC 권장
     **/
    @TransactionalEventListener(value = ConfirmDocumentFinalAcceptDecisionEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(ConfirmDocumentFinalAcceptDecisionEvent event) throws JsonProcessingException {
        ConfirmDocument confirmDocument = event.confirmDocument();
        String confirmDocumentId = confirmDocument.getConfirmDocumentId();
        DocumentType documentType = confirmDocument.getDocumentType();
        String companyId = confirmDocument.getCompanyId();
        log.info("\n [PROCESS:FINAL ACCEPT]confirmDocumentId:{}", confirmDocumentId);

        if (confirmDocument.anyApprovalNotAccepted()) {
            log.error("confirmDocumentId:{} present reject flag, so cannot process accept logic", confirmDocumentId);
            throw new ConfirmDocumentException("잘못된 접근입니다. 관리자에게 문의하세요.");
        }

        boolean thirdPartyApiResponseSuccess = false; // third-party API 정상 응답 여부

        SimpleRestClient simpleRestClient = new SimpleRestClient();
        switch (documentType) {
            case VAC -> {
                try {
                    Long vacationId = ResourceIdExtractor.execute(confirmDocumentId, documentType, companyId);
                    ResponseEntity<String> response = simpleRestClient.postForEntity("http://localhost:8080/api/vacations/{vacation-id}/vacation-status",
                            new ConfirmStatusChangeRequest("confirm-server", "APPROVED"),
                            String.class,
                            vacationId);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        thirdPartyApiResponseSuccess = true;
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            case WRK -> {
                try {
                    // PK 추출
                    Long workTicketPk = ResourceIdExtractor.execute(confirmDocumentId, documentType, companyId);

                    // GW 서버로 보낼 requestBody 파라미터 리스트
                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("workStatus", "ACCEPT");

                    ResponseResult response = simpleRestClient.patch("http://localhost:8080/api/work-tickets/{work-ticket-pk}/complete-confirm",
                            requestBody,
                            workTicketPk);

                    if (HttpStatusCode.valueOf(response.getStatus()).is2xxSuccessful()) {
                        thirdPartyApiResponseSuccess = true;
                    }
                    else {
                        log.error("{} {}", response.getStatus(), response.getMessage());
                        throw new ConfirmDocumentException("작업 티켓 후속 처리 과정에서 정상적인 응답을 받지 못했습니다." + response.getMessage());
                    }
                } catch (Exception e) {
                    log.error("", e);
                    throw e;
                }
            }
            default -> {
                log.info("documentType:{} 처리 로직은 미구현되어 있습니다.", documentType);
            }
        }

        if (thirdPartyApiResponseSuccess) {
            // 결재 문서 상태 변경 및 최종 승인/반려 시간 지정
            // WRITE QUERY : JPA dirty checking
            confirmDocument.processCompletedConfirmDocument(ConfirmStatus.ACCEPT, LocalDateTime.now());
        }
    }

    @TransactionalEventListener(value = ConfirmDocumentRejectDecisionEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(ConfirmDocumentRejectDecisionEvent event) throws JsonProcessingException {
        ConfirmDocument confirmDocument = event.confirmDocument();
        String confirmDocumentId = confirmDocument.getConfirmDocumentId();
        DocumentType documentType = confirmDocument.getDocumentType();
        String companyId = confirmDocument.getCompanyId();
        log.info("\n [PROCESS:REJECT] confirmDocumentId:{}", confirmDocumentId);

        SimpleRestClient simpleRestClient = new SimpleRestClient();

        boolean thirdPartyApiResponseSuccess = false;

        switch (documentType) {
            case VAC -> {
                try {
                    Long vacationId = ResourceIdExtractor.execute(confirmDocumentId, documentType, companyId);
                    ResponseEntity<String> response = simpleRestClient.postForEntity("http://localhost:8080/api/vacations/{vacation-id}/vacation-status",
                            new ConfirmStatusChangeRequest("confirm-server", "REJECT"),
                            String.class,
                            vacationId);
                    if (response.getStatusCode().is2xxSuccessful()) {
                        thirdPartyApiResponseSuccess = true;
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            case WRK -> {
                try {
                    // PK 추출
                    Long workTicketPk = ResourceIdExtractor.execute(confirmDocumentId, documentType, companyId);
                    // GW 서버로 보낼 requestBody 파라미터 리스트
                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("workStatus", "REJECT_FROM_REQUESTER");

                    ResponseResult response = simpleRestClient.patch("http://localhost:8080/api/work-tickets/{work-ticket-pk}/complete-confirm",
                            requestBody,
                            workTicketPk);

                    if (HttpStatusCode.valueOf(response.getStatus()).is2xxSuccessful()) {
                        thirdPartyApiResponseSuccess = true;
                    }
                } catch (Exception e) {
                    log.error("", e);
                    throw e;
                }
            }
            default -> {
                log.info("documentType:{} 처리 로직은 미구현되어 있습니다.", documentType);
            }
        }

        if (thirdPartyApiResponseSuccess) {
            // 결재 문서 상태 변경 및 최종 승인/반려 시간 지정
            // WRITE QUERY : JPA dirty checking
            confirmDocument.processCompletedConfirmDocument(ConfirmStatus.REJECT, LocalDateTime.now());
        }

    }
}
