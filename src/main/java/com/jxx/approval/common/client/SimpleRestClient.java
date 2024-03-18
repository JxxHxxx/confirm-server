package com.jxx.approval.common.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
public class SimpleRestClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SimpleRestClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public <T> T post(String url, Object request, Class<T> responseType, Object... uriVariable) throws JsonProcessingException {
        String stringResponse = null;
        try {
            stringResponse = restTemplate.postForObject(url, request, String.class, uriVariable);
        } catch (RestClientException e) {
            log.error("결재 서버 연결에 실패했습니다.", e);
            throw new RuntimeException(e);
        }

        return objectMapper.readValue(stringResponse, responseType); // 이 메서드가 리턴하는 값을 타입으로 하고 싶음
    }

    public <T> T get(String url, Class<T> responseType, Object... uriVariable) throws JsonProcessingException {
        String stringResponse = restTemplate.getForObject(url, String.class, uriVariable);

        return objectMapper.readValue(stringResponse, responseType); // 이 메서드가 리턴하는 값을 타입으로 하고 싶음
    }

    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType) throws JsonProcessingException {
        ResponseEntity<T> response = restTemplate.getForEntity(url, responseType);

//        if (response.getStatusCode().is4xxClientError()) {
//            throw new ServerToServerException("서버 통신 중 예외가 발생하였습니다.");
//        };
        return response;
    }
}