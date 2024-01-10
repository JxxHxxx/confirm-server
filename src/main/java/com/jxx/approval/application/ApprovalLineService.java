package com.jxx.approval.application;

import com.jxx.approval.domain.ApprovalLine;
import com.jxx.approval.domain.ConfirmDocument;
import com.jxx.approval.domain.ConfirmDocumentRepository;
import com.jxx.approval.dto.request.ApproverEnrollForm;
import com.jxx.approval.dto.response.ApproverServiceResponse;
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
    public List<ApproverServiceResponse> enrollApprovers(List<ApproverEnrollForm> enrollForms, Long confirmDocumentPk) {
        List<ApprovalLine> approvalLines = enrollForms.stream()
                .map(form -> new ApprovalLine(form.approvalOrder(), form.approvalId(), null))
                .toList();

        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(confirmDocumentPk)
                .orElseThrow(() -> new IllegalArgumentException());

        approvalLines.forEach(approvalLine -> approvalLine.setConfirmDocument(confirmDocument));

        List<ApprovalLine> savedApprovalLines = approvalLineRepository.saveAll(approvalLines);

        return savedApprovalLines.stream()
                .map(approvalLine -> new ApproverServiceResponse(
                        approvalLine.getPk(),
                        approvalLine.getApprovalOrder(),
                        approvalLine.getApprovalId(),
                        approvalLine.getApproveStatus()))
                .toList();
    }
}
