package com.jxx.approval.confirm.listener;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;

@Slf4j
class ConfirmDocumentEventListenerTest {

    @Test
    void test() {
        MultiValueMap<String, String> queryPrams = new HttpHeaders();
        HashMap<String, Object> pathVariables = new HashMap<>();
        pathVariables.put("workTicketId", "WRK00035");
        pathVariables.put("detail", "DET00035");

        queryPrams.add("memberId", "SPY00035");
        queryPrams.add("orgId", "ORGSPY00035");

        String stringUri = UriComponentsBuilder
                .fromUriString("/work-tickets/{workTicketId}/detail/{detail}")
                .scheme("http")
                .host("localhost")
                .port(8080)
                .queryParams(queryPrams)
                .uriVariables(pathVariables)
                .build().toString();

        log.info("stringUri {}", stringUri);

    }
}