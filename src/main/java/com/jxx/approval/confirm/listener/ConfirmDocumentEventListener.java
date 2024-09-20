package com.jxx.approval.confirm.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.jxx.approval.common.client.SimpleRestClient;
import com.jxx.approval.confirm.domain.document.ConfirmDocument;
import com.jxx.approval.confirm.domain.document.ConfirmDocumentException;
import com.jxx.approval.confirm.domain.document.ConfirmStatus;
import com.jxx.approval.confirm.domain.document.DocumentType;
import com.jxx.approval.confirm.dto.request.ConfirmStatusChangeRequest;
import com.jxx.approval.confirm.infra.ConfirmDocumentRepository;
import com.jxx.approval.vendor.vacation.ResourceIdExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmDocumentEventListener {
    private final ConfirmDocumentRepository confirmDocumentRepository;

    /**
     * 현재 결재 문서가 결재자가 승인/반려를 할 수 있는 상태인지를 검증
     **/
    @TransactionalEventListener(value = ApproveStatusChangedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(ApproveStatusChangedEvent event) {
        String confirmDocumentId = event.getConfirmDocumentId();
        // 로깅 작업 해야함
        ConfirmDocument confirmDocument = confirmDocumentRepository.findWithContent(confirmDocumentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다."));

        if (confirmDocument.confirmStatusNotBelongIn(event.getConfirmStatus())) {
            throw new ConfirmDocumentException("승인자의 요청을 처리할 수 없습니다. 요청 종류 :" + event.getRequestType(), event.getApprovalLineId());
        }
    }

    /**
     * 결재 문서 최종 승인 시 발생하는 이벤트 처리 로직
     **/
    @TransactionalEventListener(value = ConfirmDocumentFinalAcceptDecisionEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(ConfirmDocumentFinalAcceptDecisionEvent event) throws JsonProcessingException {
        log.info("cdp:{} eventType", event.confirmDocumentId());
        ConfirmDocument confirmDocument = confirmDocumentRepository.findWithContent(event.confirmDocumentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다."));

        if (confirmDocument.anyApprovalNotAccepted()) {
            log.warn("cdp:{} something wrong", confirmDocument.getPk());
            throw new ConfirmDocumentException("잘못된 접근입니다. 관리자에게 문의하세요.");
        }

        SimpleRestClient simpleRestClient = new SimpleRestClient();
        DocumentType documentType = event.documentType();
        switch (documentType) {
            case VAC -> {
                try {
                    Long vacationId = ResourceIdExtractor.execute(event.confirmDocumentId(), documentType, event.companyId());
                    simpleRestClient.postForEntity("http://localhost:8080/api/vacations/{vacation-id}/vacation-status",
                            new ConfirmStatusChangeRequest("confirm-server", "APPROVED"),
                            String.class,
                            vacationId);
                    // 결재 문서 상태 변경 및 최종 승인/반려 시간 지정
                    confirmDocument.processCompletedConfirmDocument(ConfirmStatus.ACCEPT, event.completedTime());
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            case WRK -> {
                try {
                    Map<String, Object> requestBody = new HashMap<>();
                    // PK 추출
                    Long workTicketPk = ResourceIdExtractor.execute(event.confirmDocumentId(), documentType, event.companyId());
                    // GW 서버로 보낼 requestBody 파라미터 리스트
                    requestBody.put("workStatus", "ACCEPT");

                    simpleRestClient.patch("http://localhost:8080/api/work-tickets/{work-ticket-pk}/complete-confirm",
                            requestBody,
                            String.class,
                            workTicketPk);
                    // 결재 문서 상태 변경 및 최종 승인/반려 시간 지정
                    confirmDocument.processCompletedConfirmDocument(ConfirmStatus.ACCEPT, event.completedTime());
                } catch (MismatchedInputException e) {
                    log.error("파싱 과정에서 오류 발생으로 정상 응답하여 결재 문서 상태를 ACCEPT로 변경합니다.", e);
                    confirmDocument.processCompletedConfirmDocument(ConfirmStatus.ACCEPT, event.completedTime());
                }
                catch (Exception e) {
                    log.error("", e);
                    throw e;
                }

            }
            default -> {
                log.info("documentType:{} 처리 로직은 미구현되어 있습니다.", documentType);
            }
        }
    }

    @TransactionalEventListener(value = ConfirmDocumentRejectDecisionEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(ConfirmDocumentRejectDecisionEvent event) throws JsonProcessingException {
        log.info("cdp:{} eventType", event.confirmDocumentId());
        ConfirmDocument confirmDocument = confirmDocumentRepository.findWithContent(event.confirmDocumentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다."));

        SimpleRestClient simpleRestClient = new SimpleRestClient();
        DocumentType documentType = event.documentType();
        switch (documentType) {
            case VAC -> {
                try {
                    Long vacationId = ResourceIdExtractor.execute(event.confirmDocumentId(), documentType, event.companyId());
                    simpleRestClient.postForEntity("http://localhost:8080/api/vacations/{vacation-id}/vacation-status",
                            new ConfirmStatusChangeRequest("confirm-server", "REJECT"),
                            String.class,
                            vacationId);
                    // 결재 문서 상태 변경 및 최종 승인/반려 시간 지정
                    confirmDocument.processCompletedConfirmDocument(ConfirmStatus.REJECT, event.completedTime());
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            case WRK -> {
                try {
                    Map<String, Object> requestBody = new HashMap<>();
                    // PK 추출
                    Long workTicketPk = ResourceIdExtractor.execute(event.confirmDocumentId(), documentType, event.companyId());
                    // GW 서버로 보낼 requestBody 파라미터 리스트
                    requestBody.put("workStatus", "REJECT_FROM_REQUESTER");

                    simpleRestClient.patch("http://localhost:8080/api/work-tickets/{work-ticket-pk}/complete-confirm",
                            requestBody,
                            String.class,
                            workTicketPk);
                    // 결재 문서 상태 변경 및 최종 승인/반려 시간 지정
                    confirmDocument.processCompletedConfirmDocument(ConfirmStatus.REJECT, event.completedTime());
                } catch (MismatchedInputException e) {
                    log.error("파싱 과정에서 오류 발생으로 정상 응답하여 결재 문서 상태를 REJECT 변경합니다.", e);
                    confirmDocument.processCompletedConfirmDocument(ConfirmStatus.REJECT, event.completedTime());
                }
                catch (Exception e) {
                    log.error("", e);
                    throw e;
                }

            }
            default -> {
                log.info("documentType:{} 처리 로직은 미구현되어 있습니다.", documentType);
            }
        }

    }
}
