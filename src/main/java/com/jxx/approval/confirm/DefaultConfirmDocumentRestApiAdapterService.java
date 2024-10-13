package com.jxx.approval.confirm;

import com.jxx.approval.common.client.SimpleRestClient;
import com.jxx.approval.confirm.application.ConfirmDocumentRestApiAdapterService;
import com.jxx.approval.confirm.domain.connect.ConnectionElement;
import com.jxx.approval.confirm.domain.connect.RestApiConnection;
import com.jxx.approval.confirm.domain.document.*;
import com.jxx.approval.confirm.dto.response.ResponseResult;
import com.jxx.approval.confirm.infra.RestApiConnectionRepository;
import com.jxx.approval.vendor.vacation.ResourceIdExtractor;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultConfirmDocumentRestApiAdapterService implements ConfirmDocumentRestApiAdapterService {

    private final RestApiConnectionRepository restApiConnectionRepository;
    private final EntityManager entityManager;

    @Override
    public boolean call(ConfirmDocument confirmDocument, String triggerType) {
        String confirmDocumentId = confirmDocument.getConfirmDocumentId();
        DocumentType documentType = confirmDocument.getDocumentType();
        String companyId = confirmDocument.getCompanyId();

        log.info("\n [EVENT-START][DOCID:{} DOCTYPE:{} TRIGTYPE:{}]", confirmDocumentId, documentType, triggerType);

        // 추상화 START
        SimpleRestClient simpleRestClient = new SimpleRestClient();
        List<RestApiConnection> restApiConnections = restApiConnectionRepository.fetchWithConnectionElements(documentType, triggerType);

        RestApiConnection connection = restApiConnections.get(0);
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
                default -> log.error("ElementValueType: {}  is not exist", connectionElement.getElementValueType());
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
                    HttpStatusCode statusCode = response.getStatusCode();
                    if (statusCode.is2xxSuccessful()) {
                        return true;
                    } else {
                        log.info("{} 상태 코드 응답을 받았습니다. 처리 실패", statusCode.value());
                        return false;
                    }
                } catch (Exception e) {
                    log.error("예상치 않은 예외 발생", e);
                    throw new ConfirmDocumentException("오류 발생");
                }
            }
            case "PATCH" -> {
                ResponseResult response = null;
                try {
                    response = simpleRestClient.patch(url, httpRequestBody);
                    Integer statusCode = response.getStatus();
                    if (HttpStatusCode.valueOf(statusCode).is2xxSuccessful()) {
                        return true;
                    }
                    else {
                        log.info("{} 상태 코드 응답을 받았습니다. 처리 실패", statusCode);
                        return false;
                    }
                } catch (Exception e) {
                    log.error("예상치 않은 예외 발생", e);
                    throw new ConfirmDocumentException("오류 발생");
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
}
