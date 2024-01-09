package com.jxx.approval.application;

import com.jxx.approval.domain.Approver;
import com.jxx.approval.domain.ConfirmDocument;
import com.jxx.approval.domain.ConfirmDocumentRepository;
import com.jxx.approval.dto.request.ApproverEnrollForm;
import com.jxx.approval.dto.response.ApproverServiceResponse;
import com.jxx.approval.infra.ApproverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApproverService {

    private final ApproverRepository approverRepository;
    private final ConfirmDocumentRepository confirmDocumentRepository;

    @Transactional
    public List<ApproverServiceResponse> enrollApprovers(List<ApproverEnrollForm> enrollForms, Long confirmDocumentPk) {
        List<Approver> approvers = enrollForms.stream()
                .map(form -> new Approver(form.approvalOrder(), form.approvalId(), null))
                .toList();

        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(confirmDocumentPk)
                .orElseThrow(() -> new IllegalArgumentException());

        approvers.forEach(approver -> approver.setConfirmDocument(confirmDocument));

        List<Approver> savedApprovers = approverRepository.saveAll(approvers);

        return savedApprovers.stream()
                .map(approver -> new ApproverServiceResponse(
                        approver.getPk(),
                        approver.getApprovalOrder(),
                        approver.getApprovalId(),
                        approver.getApproveStatus()))
                .toList();
    }
}
