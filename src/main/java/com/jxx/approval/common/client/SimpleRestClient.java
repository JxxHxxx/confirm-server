package com.jxx.approval.common.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        return response;
    }

    public <T> T patch(String url, Object request, Class<T> responseType, Object... uriVariable) throws JsonProcessingException {
        String stringResponse = restTemplate.patchForObject(url, request, String.class, uriVariable);
        return objectMapper.readValue(stringResponse, responseType);
    }
}