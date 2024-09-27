package com.jxx.approval.confirm.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.approval.common.client.SimpleRestClient;
import com.jxx.approval.confirm.domain.connect.RestApiConnection;
import com.jxx.approval.confirm.domain.connect.ConnectionElement;
import com.jxx.approval.confirm.domain.document.ConfirmDocument;
import com.jxx.approval.confirm.domain.document.ConfirmDocumentException;
import com.jxx.approval.confirm.domain.document.ConfirmStatus;
import com.jxx.approval.confirm.domain.document.DocumentType;
import com.jxx.approval.confirm.dto.request.ConfirmStatusChangeRequest;
import com.jxx.approval.confirm.dto.response.ResponseResult;
import com.jxx.approval.confirm.infra.RestApiConnectionRepository;
import com.jxx.approval.vendor.vacation.ResourceIdExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmDocumentEventListener {

    private final RestApiConnectionRepository restApiConnectionRepository;

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
//    @TransactionalEventListener(value = ConfirmDocumentFinalAcceptDecisionEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
//    public void handleConfirmDocumentFinalAcceptDecisionEvent(ConfirmDocumentFinalAcceptDecisionEvent event) throws JsonProcessingException {
//        ConfirmDocument confirmDocument = event.confirmDocument();
//        String confirmDocumentId = confirmDocument.getConfirmDocumentId();
//        DocumentType documentType = confirmDocument.getDocumentType();
//        String companyId = confirmDocument.getCompanyId();
//        log.info("\n [PROCESS:FINAL ACCEPT]confirmDocumentId:{}", confirmDocumentId);
//
//        if (confirmDocument.anyApprovalNotAccepted()) {
//            log.error("confirmDocumentId:{} present reject flag, so cannot process accept logic", confirmDocumentId);
//            throw new ConfirmDocumentException("잘못된 접근입니다. 관리자에게 문의하세요.");
//        }
//
//        boolean thirdPartyApiResponseSuccess = false; // third-party API 정상 응답 여부
//
//        SimpleRestClient simpleRestClient = new SimpleRestClient();
//        switch (documentType) {
//            case VAC -> {
//                try {
//                    Long vacationId = ResourceIdExtractor.execute(confirmDocumentId, documentType, companyId);
//                    ResponseEntity<String> response = simpleRestClient.postForEntity("http://localhost:8080/api/vacations/{vacation-id}/vacation-status",
//                            new ConfirmStatusChangeRequest("confirm-server", "APPROVED"),
//                            String.class,
//                            vacationId);
//
//                    if (response.getStatusCode().is2xxSuccessful()) {
//                        thirdPartyApiResponseSuccess = true;
//                    }
//                } catch (Exception e) {
//                    log.error("", e);
//                }
//            }
//
//            case WRK -> {
//                try {
//                    // PK 추출
//                    Long workTicketPk = ResourceIdExtractor.execute(confirmDocumentId, documentType, companyId);
//
//                    RestApiConnection connection = restApiConnectionRepository.findByDocumentTypeAndTriggerType(documentType, "ACCEPT").get();
//                    List<ConnectionElement> connectionElements = connection.getConnectionElements();
//                    Map<String, Object> newRequestBody = new HashMap<>();
//                    connectionElements.forEach(cp -> newRequestBody.put(cp.getElementrKey(), cp.getElementValue()));
//                    ResponseResult response = simpleRestClient.patch(connection.getHost() + "/" + connection.getUrl(), newRequestBody, workTicketPk);
//
//                    if (HttpStatusCode.valueOf(response.getStatus()).is2xxSuccessful()) {
//                        thirdPartyApiResponseSuccess = true;
//                    }
//                    else {
//                        log.error("{} {}", response.getStatus(), response.getMessage());
//                        throw new ConfirmDocumentException("작업 티켓 후속 처리 과정에서 정상적인 응답을 받지 못했습니다." + response.getMessage());
//                    }
//                } catch (Exception e) {
//                    log.error("", e);
//                    throw e;
//                }
//            }
//            default -> {
//                log.info("documentType:{} 처리 로직은 미구현되어 있습니다.", documentType);
//            }
//        }
//
//        if (thirdPartyApiResponseSuccess) {
//            // 결재 문서 상태 변경 및 최종 승인/반려 시간 지정
//            // WRITE QUERY : JPA dirty checking
//            confirmDocument.processFinalDecisionConfirmDocument(ConfirmStatus.ACCEPT);
//        }
//    }

    /**
     * 결재 문서 최종 승인 시 발생하는 이벤트 처리 로직
     * 외부 통신이 발생할 수 있다. SYNC 권장
     **/
    @TransactionalEventListener(value = ConfirmDocumentFinalAcceptDecisionEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handleConfirmDocumentFinalAcceptDecisionEvent(ConfirmDocumentFinalAcceptDecisionEvent event) throws JsonProcessingException {
        // third-party API 정상 응답 여부
        ConfirmDocument confirmDocument = event.confirmDocument();
        String triggerType = event.triggerType();

        if (executeFinalAcceptAfterProcess(confirmDocument, triggerType)) {
            // 결재 문서 상태 변경 및 최종 승인/반려 시간 지정
            // WRITE QUERY : JPA dirty checking
            confirmDocument.processFinalDecisionConfirmDocument(ConfirmStatus.ACCEPT);
        }
    }

    /**
     *
     * @param confirmDocument
     * @param triggerType
     * @return true : REST API 정상 응답 / false REST API 정상 응답 실패
     * @throws JsonProcessingException
     */
    public boolean executeFinalAcceptAfterProcess(ConfirmDocument confirmDocument, String triggerType) throws JsonProcessingException {
        String confirmDocumentId = confirmDocument.getConfirmDocumentId();
        DocumentType documentType = confirmDocument.getDocumentType();
        String companyId = confirmDocument.getCompanyId();

        log.info("\n [START][EVENT:{}]confirmDocumentId:{}", triggerType, confirmDocumentId);

        // 추상화 START
        SimpleRestClient simpleRestClient = new SimpleRestClient();
        RestApiConnection connection = restApiConnectionRepository.findByDocumentTypeAndTriggerType(documentType, triggerType)
                .orElseThrow();
        List<ConnectionElement> connectionElements = connection.getConnectionElements();

        // request body 생성
        List<ConnectionElement> requestBodyConnectionElements = connectionElements.stream()
                .filter(ConnectionElement::isRequestBodyType)
                .toList();

        Map<String, Object> httpRequestBody = new HashMap<>();
        requestBodyConnectionElements.forEach(cp -> httpRequestBody.put(cp.getElementKey(), cp.getElementValue()));

        List<ConnectionElement> pathVariableConnectionElements = connectionElements.stream()
                .filter(ConnectionElement::isPathVariableType)
                .toList();

        HashMap<String, Object> pathVariables = new HashMap<>();
        // TODO 우선 RESOURCE_ID  pathVariable 만 처리하도록 하였음, 변수 타입 추상화 필요
        for (ConnectionElement connectionElement : pathVariableConnectionElements) {
            String elementKey = connectionElement.getElementKey();
            String elementValue = connectionElement.getElementValue();

            switch (connectionElement.getElementValueType()) {
                case RESOURCE_ID ->
                        pathVariables.put(elementKey, ResourceIdExtractor.execute(confirmDocumentId, documentType, companyId));
                case CONST -> pathVariables.put(elementKey, elementValue);
                case VARIABLE -> log.info("미구현");
                default -> log.error("ElementValueType is not exist");
            }
        }

        // TODO Query String 추상화 필요
        MultiValueMap<String, String> queryPrams = new HttpHeaders();

        String url = UriComponentsBuilder.fromUriString(connection.getPath())
                .scheme(connection.getScheme())
                .host(connection.getHost())
                .port(connection.getPort())
                .queryParams(queryPrams)
                .uriVariables(pathVariables)
                .build()
                .toString();

        // TODO 우선 GET,POST,PATCH 만 구현
        switch (connection.getMethodType()) {
            case "POST" -> {
                try {
                    ResponseEntity<String> response = simpleRestClient.postForEntity(url, httpRequestBody, String.class);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    log.error("", e);
                    throw new ConfirmDocumentException("오류 발생");
                }
            }
            case "PATCH" -> {
                ResponseResult response = simpleRestClient.patch(url, httpRequestBody);

                if (HttpStatusCode.valueOf(response.getStatus()).is2xxSuccessful()) {
                    return true;
                } else {
                    log.error("{} {}", response.getStatus(), response.getMessage());
                    throw new ConfirmDocumentException(response.getMessage());
                }
            }
            case "GET" -> {
                log.info("GET 미구현");
                return false;
            }
            default -> {
                return false;
            }
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
            confirmDocument.processFinalDecisionConfirmDocument(ConfirmStatus.REJECT);
        }

    }
}
