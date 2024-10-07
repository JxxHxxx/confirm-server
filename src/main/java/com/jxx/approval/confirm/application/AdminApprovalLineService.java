package com.jxx.approval.confirm.application;

import com.jxx.approval.confirm.domain.line.ApprovalLine;
import com.jxx.approval.confirm.dto.response.ApprovalLineServiceDto;
import com.jxx.approval.confirm.infra.ApprovalLineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminApprovalLineService {
    private final ApprovalLineRepository approvalLineRepository;

    public List<ApprovalLineServiceDto> findByConfirmDocumentId(String confirmDocumentId) {
        List<ApprovalLine> approvalLines = approvalLineRepository.fetchByConfirmDocumentId(confirmDocumentId);

        return approvalLines.stream()
                .map(ap -> new ApprovalLineServiceDto(
                        ap.getPk(),
                        ap.getApprovalOrder(),
                        ap.getApprovalLineId(),
                        ap.getApproveStatus(),
                        ap.getApprovalName(),
                        ap.getApproveTime()))
                .toList();
    }
}
