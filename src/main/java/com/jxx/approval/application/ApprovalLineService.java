package com.jxx.approval.application;

import com.jxx.approval.domain.*;
import com.jxx.approval.dto.request.ApprovalInformationForm;
import com.jxx.approval.dto.request.ConfirmDocumentCancelForm;
import com.jxx.approval.dto.response.ConfirmDocumentServiceResponse;
import com.jxx.approval.dto.response.ConfirmServiceResponse;
import com.jxx.approval.infra.ConfirmDocumentRepository;
import com.jxx.approval.dto.request.ApproverEnrollForm;
import com.jxx.approval.dto.response.ApprovalLineServiceResponse;
import com.jxx.approval.infra.ApprovalLineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jxx.approval.domain.ConfirmStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalLineService {

    private final ApprovalLineRepository approvalLineRepository;
    private final ConfirmDocumentRepository confirmDocumentRepository;

    // 리팩토링 29행 stream 에서 null 들어가서 혼란스러움, 34행 이 제일먼저 선언되도 되듯? 37행 필요 없어보임, 29행 생성자에서 박으면 될듯?
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
    public ApprovalLineServiceResponse approveConfirmDocument(Long confirmDocumentPk, ApprovalInformationForm form) {
        // 리스너로 ConfirmDocument 체킹 해야됨
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
    public ApprovalLineServiceResponse rejectConfirmDocument(Long confirmDocumentPk, ApprovalInformationForm form) {
        // 리스너로 ConfirmDocument 체킹 해야됨

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

    @Transactional
    public ConfirmDocumentServiceResponse cancelConfirmDocument(Long confirmDocumentPk, ConfirmDocumentCancelForm form) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(confirmDocumentPk)
                .orElseThrow();

        // 취소 가능한 자인지
        confirmDocument.isDocumentRequester(form.requesterId());
        // 취소 가능한 상태인지
        confirmDocument.verifyCancelable();
        // 결재 문서 상태 변경
        confirmDocument.changeConfirmStatus(CANCEL);
        return new ConfirmDocumentServiceResponse(
                confirmDocument.getPk(),
                confirmDocument.getConfirmDocumentId(),
                confirmDocument.getCompanyId(),
                confirmDocument.getDepartmentId(),
                confirmDocument.getCreateSystem(),
                confirmDocument.getConfirmStatus(),
                confirmDocument.getDocumentType(),
                confirmDocument.getRequesterId());
    }
}
