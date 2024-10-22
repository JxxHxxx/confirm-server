package com.jxx.approval.confirm;

import com.jxx.approval.common.client.SimpleRestClient;
import com.jxx.approval.confirm.application.ConfirmDocumentRestApiAdapterService;
import com.jxx.approval.confirm.domain.AdminClientException;
import com.jxx.approval.confirm.domain.connect.ConnectionElement;
import com.jxx.approval.confirm.domain.connect.RestApiConnection;
import com.jxx.approval.confirm.domain.document.*;
import com.jxx.approval.confirm.dto.response.ResponseResult;
import com.jxx.approval.confirm.infra.RestApiConnectionRepository;
import com.jxx.approval.vendor.vacation.ResourceIdExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

    @Override
    public boolean call(ConfirmDocument confirmDocument, String triggerType) {
        String confirmDocumentId = confirmDocument.getConfirmDocumentId();
        DocumentType documentType = confirmDocument.getDocumentType();
        String companyId = confirmDocument.getCompanyId();

        log.info("\n [EVENT-START][DOCID:{} DOCTYPE:{} TRIGTYPE:{}]", confirmDocumentId, documentType, triggerType);

        // 추상화 START
        RestApiConnection connection = null;
        try {
            connection = restApiConnectionRepository.fetchWithConnectionElements(documentType, triggerType).get(0);
        } catch (ArrayIndexOutOfBoundsException exception) {
            log.error("documentType:{} triggerType:{} 을 만족하는 restApiConnection 존재하지 않음" ,documentType, triggerType, exception);
            throw new AdminClientException("documentType:" + documentType + "triggerType: " + triggerType + "을 만족하는 restApiConnection 존재하지 않음");
        }

        Map<String, Object> requestBody = buildRequestBody(connection); // request body 생성
        String url = buildUrl(confirmDocumentId, companyId, connection); // url 생성
        return callApi(connection, requestBody, url); // api 호출
    }

    private Map<String, Object> buildRequestBody(RestApiConnection connection) {
        List<ConnectionElement> requestBodyConnectionElements = connection.getConnectionElements()
                .stream()
                .filter(ConnectionElement::isRequestBodyType)
                .toList();

        Map<String, Object> requestBody = new HashMap<>();
        requestBodyConnectionElements.forEach(cp -> requestBody.put(cp.getElementKey(), cp.getElementValue()));
        return requestBody;
    }

    private String buildUrl(String confirmDocumentId, String companyId, RestApiConnection connection) {
        List<ConnectionElement> pathVariableConnectionElements = connection.getConnectionElements()
                .stream()
                .filter(ConnectionElement::isPathVariableType)
                .toList();

        HashMap<String, Object> pathVariables = new HashMap<>();
        // TODO 우선 RESOURCE_ID  pathVariable 만 처리하도록 하였음, 변수 타입 추상화 필요
        for (ConnectionElement connectionElement : pathVariableConnectionElements) {
            String elementKey = connectionElement.getElementKey();
            String elementValue = connectionElement.getElementValue();

            switch (connectionElement.getElementValueType()) {
                case RESOURCE_ID ->
                        pathVariables.put(elementKey, ResourceIdExtractor.execute(confirmDocumentId, connection.getDocumentType(), companyId));
                case CONST -> pathVariables.put(elementKey, elementValue);
                case VARIABLE -> log.info("미구현");
                default -> log.error("ElementValueType: {}  is not exist", connectionElement.getElementValueType());
            }
        }

        // TODO Query String 추상화 필요
        MultiValueMap<String, String> queryPrams = new HttpHeaders();

        return UriComponentsBuilder.fromUriString(connection.getPath())
                .scheme(connection.getScheme())
                .host(connection.getHost())
                .port(connection.getPort())
                .queryParams(queryPrams)
                .uriVariables(pathVariables)
                .build()
                .toString();
    }

    /**
     * @return true : API 정상 응답 O / false : API 정상 응답 X
     *
     **/
    private static boolean callApi(RestApiConnection connection, Map<String, Object> httpRequestBody, String url) {
        SimpleRestClient simpleRestClient = new SimpleRestClient();
        switch (connection.getMethodType()) {
            case "POST" -> {
                try {
                    ResponseEntity<String> response = simpleRestClient.postForEntity(url, httpRequestBody, String.class);
                    HttpStatusCode statusCode = response.getStatusCode();
                    log.info("response statusCode {}", statusCode);
                    return statusCode.is2xxSuccessful();
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
                    log.info("response statusCode {}", statusCode);
                    return HttpStatusCode.valueOf(statusCode).is2xxSuccessful();
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
