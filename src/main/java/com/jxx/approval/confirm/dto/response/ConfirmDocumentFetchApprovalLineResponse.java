package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.ApproveStatus;
import com.jxx.approval.confirm.domain.ConfirmStatus;
import com.jxx.approval.confirm.domain.DocumentType;

import java.time.LocalDateTime;

public record ConfirmDocumentFetchApprovalLineResponse(
        Long pk,
        String confirmDocumentId,
        String companyId,
        String departmentId,
        String createSystem,
        ConfirmStatus confirmStatus,
        DocumentType documentType,
        String requesterId,
        LocalDateTime createTime,
        Long confirmDocumentContentPk,
        Long approvalLinePk,
        String approvalLineId,
        ApproveStatus approveStatus,
        LocalDateTime approvalTime

) {
}
