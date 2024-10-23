package com.jxx.approval.confirm.application;

import com.jxx.approval.common.client.SimpleRestClient;
import com.jxx.approval.confirm.domain.connect.ConnectionElement;
import com.jxx.approval.confirm.domain.connect.RestApiConnResponseCode;
import com.jxx.approval.confirm.domain.connect.RestApiConnection;
import com.jxx.approval.confirm.domain.connect.RestApiConnectionException;
import com.jxx.approval.confirm.domain.document.ConfirmDocument;
import com.jxx.approval.confirm.domain.document.DocumentType;
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

import static com.jxx.approval.confirm.domain.connect.RestApiConnResponseCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultConfirmDocumentRestApiAdapterService implements ConfirmDocumentRestApiAdapterService {

    private final RestApiConnectionRepository restApiConnectionRepository;

    @Override
    public RestApiConnResponseCode call(ConfirmDocument confirmDocument, String triggerType) {
        String confirmDocumentId = confirmDocument.getConfirmDocumentId();
        DocumentType documentType = confirmDocument.getDocumentType();
        String companyId = confirmDocument.getCompanyId();
        RestApiConnection connection = null;
        try {
            List<RestApiConnection> connections = restApiConnectionRepository.fetchWithConnectionElements(documentType, triggerType);
            // documentType, triggerType 조건에 만족하는 RestApiConnection 이 스토어에 저장되어 있지 않은 케이스
            if (connections.isEmpty()) {
                throw new RestApiConnectionException(RCF02);
            }

            connection = connections.get(0);
            // 연동 API가 등록되어 있지만 사용하지 않는 케이스
            if (!connection.isUsed()) {
                return RCS02;
            }

        } catch (Exception exception) {
            throw new RestApiConnectionException(RCF90);
        }

        Map<String, Object> requestBody = buildRequestBody(connection); // request body 생성
        String url = buildUrl(confirmDocumentId, companyId, connection); // url 생성

        return callApi(connection, requestBody, url);
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

    private RestApiConnResponseCode callApi(RestApiConnection connection, Map<String, Object> httpRequestBody, String url) {
        SimpleRestClient simpleRestClient = new SimpleRestClient();
        switch (connection.getMethodType()) {
            case "POST" -> {
                try {
                    ResponseEntity<String> response = simpleRestClient.postForEntity(url, httpRequestBody, String.class);
                    HttpStatusCode statusCode = response.getStatusCode();

                    if (statusCode.is2xxSuccessful()) {
                        return RCS01;
                    }
                    else { // 4xx, 5xx 응답 예외
                        throw new RestApiConnectionException(RCF01, statusCode.value());
                    }

                } catch (Exception e) {
                    throw new RestApiConnectionException(RCF90, e);
                }
            }
            case "PATCH" -> {
                try {
                    ResponseResult response = simpleRestClient.patch(url, httpRequestBody);
                    Integer statusCode = response.getStatus();
                    if (HttpStatusCode.valueOf(statusCode).is2xxSuccessful()) {
                        return RCS01;
                    }
                    else { // 4xx, 5xx 응답 예외
                        throw new RestApiConnectionException(RCF01, statusCode);
                    }
                } catch (Exception e) {
                    throw new RestApiConnectionException(RCF90, e);
                }
            }
            case "GET" -> {
                return RCF99;
            }
            default -> {
                return RCF99;
            }
        }
    }
}
