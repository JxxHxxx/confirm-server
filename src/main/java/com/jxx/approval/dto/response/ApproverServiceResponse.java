package com.jxx.approval.dto.response;

import com.jxx.approval.domain.ApproveStatus;

public record ApproverServiceResponse(
        Long pk,
        Integer approverOrder,
        String approverId,
        ApproveStatus approveStatus
) {
}
