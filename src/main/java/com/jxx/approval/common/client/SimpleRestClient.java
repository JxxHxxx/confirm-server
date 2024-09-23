package com.jxx.approval.common.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxx.approval.confirm.dto.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class SimpleRestClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SimpleRestClient() {
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        this.objectMapper = new ObjectMapper();
    }

    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Object... uriVariable) throws JsonProcessingException {
        ResponseEntity<T> response = restTemplate.postForEntity(url, request, responseType, uriVariable);
        return response;
    }


    public <T> T get(String url, Class<T> responseType, Object... uriVariable) throws JsonProcessingException {
        String stringResponse = restTemplate.getForObject(url, String.class, uriVariable);

        return objectMapper.readValue(stringResponse, responseType); // 이 메서드가 리턴하는 값을 타입으로 하고 싶음
    }

    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType) throws JsonProcessingException {
        ResponseEntity<T> response = restTemplate.getForEntity(url, responseType);
        ;
        return response;
    }

    /** ResponseResult 객체는 아래와 같은 형태이다.
     * Jxx 프로젝트에서 공통으로 따르는 API 응답 구조이기 떄문에 내부 프로젝트 간에는 호환이 가능하다.
     * {
     * "status":200,
     * "message":"response success",
     * "data": ...
     * }"
     **/
    public ResponseResult patch(String url, Object request, Object... uriVariable) throws JsonProcessingException {
        return restTemplate.patchForObject(url, request, ResponseResult.class, uriVariable);
    }

    public <T> T patch(String url, Object request, Class<T> responseType, Object... uriVariable) throws JsonProcessingException {

        return restTemplate.patchForObject(url, request, responseType, uriVariable);
    }
}