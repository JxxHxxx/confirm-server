package com.jxx.approval.dto.response;

import com.jxx.approval.domain.ApproveStatus;

public record ApprovalLineServiceResponse(
        Long pk,
        Integer approvalOrder,
        String approvalId,
        ApproveStatus approveStatus
) {
}
