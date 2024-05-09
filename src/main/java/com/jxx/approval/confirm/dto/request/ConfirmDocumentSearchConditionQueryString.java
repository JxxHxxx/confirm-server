package com.jxx.approval.confirm.dto.request;

import com.jxx.approval.confirm.domain.ApproveStatus;

public record ConfirmDocumentSearchConditionQueryString(
        Long confirmDocumentPk,
        String confirmDocumentId,
        String companyId,
        String departmentId,
        String requesterId,
        String approvalId,
        String approveStatus
) {
}
