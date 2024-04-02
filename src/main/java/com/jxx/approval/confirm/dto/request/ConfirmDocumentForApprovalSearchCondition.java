package com.jxx.approval.confirm.dto.request;

import com.jxx.approval.confirm.domain.ApproveStatus;

public record ConfirmDocumentForApprovalSearchCondition(
        String approvalId,
        ApproveStatus approveStatus
) {
}
