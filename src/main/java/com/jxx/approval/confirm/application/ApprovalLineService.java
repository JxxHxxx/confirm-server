package com.jxx.approval.confirm.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.approval.common.client.SimpleRestClient;
import com.jxx.approval.confirm.domain.*;
import com.jxx.approval.confirm.dto.request.ApprovalInformationForm;
import com.jxx.approval.confirm.infra.ConfirmDocumentRepository;
import com.jxx.approval.confirm.dto.request.ApproverEnrollForm;
import com.jxx.approval.confirm.dto.response.ApprovalLineServiceResponse;
import com.jxx.approval.confirm.infra.ApprovalLineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalLineService {

    private final ApprovalLineRepository approvalLineRepository;
    private final ConfirmDocumentRepository confirmDocumentRepository;
    @Transactional
    public List<ApprovalLineServiceResponse> enrollApprovalLines(List<ApproverEnrollForm> enrollForms, String confirmDocumentId) throws JsonProcessingException {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByDocumentConfirmDocumentId(confirmDocumentId)
                .orElseThrow(() -> new IllegalArgumentException());

        // 동일 회사 구성원인지 검증해야 함

        String companyId = confirmDocument.getCompanyId();
        List<String> membersId = enrollForms.stream().map(ApproverEnrollForm::approvalId).toList();
        // 결재선에 지정된 사용자가 부서 사람인지 검증
        SimpleRestClient restClient = new SimpleRestClient();
        String url = UriComponentsBuilder.fromUriString("http://localhost:8080")
                .path("/api/companies/{company-id}/member-leaves")
                .queryParam("membersId", membersId)
                .encode()
                .buildAndExpand(companyId)
                .toString();

        ResponseEntity<List> responseEntity = restClient.getForEntity(url, List.class);
        if (responseEntity.getStatusCode().is4xxClientError()) {
            throw new ConfirmDocumentException("사내 구성원이 아닙니다.", null);
        }

        // 상신 전 상태인지 확인
        if (confirmDocument.isNotRaiseBefore()) {
            throw new ConfirmDocumentException("해당 결재 문서는 결재선을 수정할 수 없는 상태입니다." + confirmDocument.getConfirmStatus()
                    , confirmDocument.getRequesterId());
        };
        // 이미 결재선이 등록되어 있는지 확인
        List<ApprovalLine> approvalLines = approvalLineRepository.findByConfirmDocumentPk(confirmDocument.getPk());
        if (!approvalLines.isEmpty()) {
            throw new ConfirmDocumentException("이미 결재선이 지정된 결재 문서입니다.", confirmDocument.getRequesterId());
        }

        List<ApprovalLine> requestApprovalLines = enrollForms.stream()
                .map(form -> new ApprovalLine(form.approvalOrder(), form.approvalId(), confirmDocument))
                .toList();
        List<ApprovalLine> savedApprovalLines = approvalLineRepository.saveAll(requestApprovalLines);

        return savedApprovalLines.stream()
                .map(approvalLine -> new ApprovalLineServiceResponse(
                        approvalLine.getPk(),
                        approvalLine.getApprovalOrder(),
                        approvalLine.getApprovalLineId(),
                        approvalLine.getApproveStatus()))
                .toList();
    }

    @Transactional
    public ApprovalLineServiceResponse accept(Long confirmDocumentPk, ApprovalInformationForm form) {
        // 리스너로 ConfirmDocument Status 체킹 해야됨
        List<ApprovalLine> approvalLines = approvalLineRepository.findByConfirmDocumentPk(confirmDocumentPk);

        ApprovalLineManager approvalLineManager = ApprovalLineManager.builder()
                .approvalLineId(form.approvalLineId())
                .approvalLines(approvalLines)
                .build()
                .isEmptyApprovalLine();

        ApprovalLine approvalLine = approvalLineManager
                .checkBelongInApprovalLine()
                .checkApprovalLineOrder()
                .changeApproveStatus(ApproveStatus.ACCEPT);

        return new ApprovalLineServiceResponse(
                approvalLine.getPk(),
                approvalLine.getApprovalOrder(),
                approvalLine.getApprovalLineId(),
                approvalLine.getApproveStatus());
    }

    @Transactional
    public ApprovalLineServiceResponse reject(Long confirmDocumentPk, ApprovalInformationForm form) {
        // 리스너로 ConfirmDocument Status 체킹 해야됨

        // 파기된 문서인지 체크
        List<ApprovalLine> approvalLines = approvalLineRepository.findByConfirmDocumentPk(confirmDocumentPk);

        ApprovalLineManager approvalLineManager = ApprovalLineManager.builder()
                .approvalLineId(form.approvalLineId())
                .approvalLines(approvalLines)
                .build();

        approvalLineManager.isEmptyApprovalLine();

        // 자신이 이미 결정한 사안인지 체크
        ApprovalLine approvalLine = approvalLineManager
                .checkBelongInApprovalLine()
                .checkApprovalLineOrder()
                .changeApproveStatus(ApproveStatus.REJECT);

        return new ApprovalLineServiceResponse(
                approvalLine.getPk(),
                approvalLine.getApprovalOrder(),
                approvalLine.getApprovalLineId(),
                approvalLine.getApproveStatus());
    }
}
