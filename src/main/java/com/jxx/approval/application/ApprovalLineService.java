package com.jxx.approval.application;

import com.jxx.approval.domain.ApprovalLine;
import com.jxx.approval.domain.ConfirmDocument;
import com.jxx.approval.domain.ApprovalLineManager;
import com.jxx.approval.dto.request.ApprovalInformationForm;
import com.jxx.approval.infra.ConfirmDocumentRepository;
import com.jxx.approval.dto.request.ApproverEnrollForm;
import com.jxx.approval.dto.response.ApprovalLineServiceResponse;
import com.jxx.approval.infra.ApprovalLineRepository;
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
    public List<ApprovalLineServiceResponse> enrollApprovers(List<ApproverEnrollForm> enrollForms, Long confirmDocumentPk) {
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

    public ApprovalLineServiceResponse approveConfirmDocument(Long confirmDocumentPk, ApprovalInformationForm form) {
        List<ApprovalLine> approvalLines = approvalLineRepository.findByConfirmDocument(confirmDocumentPk);
        // 해당 결재 문서가 없는 경우 처리
        if (approvalLines.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않은 결재 문서입니다.");
        }

        ApprovalLineManager approvalLineManager = ApprovalLineManager.builder()
                .approvalLineId(form.approvalLineId())
                .approvalLines(approvalLines)
                .build()
                .afterPropertiesSet();

        ApprovalLine approvalLine = approvalLineManager
                .checkBelongInApprovalLine()
                .checkApprovalOrderLine()
                .acceptApprovalLine();

        return new ApprovalLineServiceResponse(
                approvalLine.getPk(),
                approvalLine.getApprovalOrder(),
                approvalLine.getApprovalLineId(),
                approvalLine.getApproveStatus());
    }
}
