package com.jxx.approval.confirm.application.server.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.approval.common.client.SimpleRestClient;
import com.jxx.approval.confirm.application.server.dto.VerifyCompanyMemberDto;
import com.jxx.approval.confirm.domain.ConfirmDocumentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.function.Consumer;

public class VerifyCompanyMemberApi implements Consumer<VerifyCompanyMemberDto> {

    @Override
    public void accept(VerifyCompanyMemberDto companyMemberDto) {
        SimpleRestClient restClient = new SimpleRestClient();
        String url = UriComponentsBuilder
                .fromUriString("http://localhost:8080")
                .path("/api/companies/{company-id}/member-leaves")
                .queryParam("membersId", companyMemberDto.membersId())
                .encode()
                .buildAndExpand(companyMemberDto.companyId())
                .toString();

        ResponseEntity<List> responseEntity = null;
        try {
            responseEntity = restClient.getForEntity(url, List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (responseEntity.getStatusCode().is4xxClientError()) {
            throw new ConfirmDocumentException("사내 구성원이 아닙니다.", null);
        }
    }
}
