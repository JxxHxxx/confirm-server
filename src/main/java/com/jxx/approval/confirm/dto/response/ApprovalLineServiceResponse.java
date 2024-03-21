package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.ApproveStatus;

public record ApprovalLineServiceResponse(
        Long approvalLinePk,
        Integer approvalOrder,
        String approvalId,
        ApproveStatus approveStatus
) {
}
