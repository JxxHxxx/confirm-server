package com.jxx.approval.confirm.application;

import com.jxx.approval.confirm.domain.ApprovalLine;
import com.jxx.approval.confirm.domain.ApprovalLineManager;
import com.jxx.approval.confirm.domain.ApproveStatus;
import com.jxx.approval.confirm.domain.ConfirmDocument;
import com.jxx.approval.confirm.dto.request.ApprovalInformationForm;
import com.jxx.approval.confirm.infra.ConfirmDocumentRepository;
import com.jxx.approval.confirm.dto.request.ApproverEnrollForm;
import com.jxx.approval.confirm.dto.response.ApprovalLineServiceResponse;
import com.jxx.approval.confirm.infra.ApprovalLineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalLineService {

    private final ApprovalLineRepository approvalLineRepository;
    private final ConfirmDocumentRepository confirmDocumentRepository;

    @Transactional
    public List<ApprovalLineServiceResponse> enrollApprovals(List<ApproverEnrollForm> enrollForms, Long confirmDocumentPk) {
        List<ApprovalLine> approvalLines = enrollForms.stream()
                .map(form -> new ApprovalLine(form.approvalOrder(), form.approvalId(), null))
                .toList();

        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(confirmDocumentPk)
                .orElseThrow(() -> new IllegalArgumentException());

        approvalLines.forEach(approvalLine -> approvalLine.setConfirmDocument(confirmDocument));

        List<ApprovalLine> savedApprovalLines = approvalLineRepository.saveAll(approvalLines);

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
