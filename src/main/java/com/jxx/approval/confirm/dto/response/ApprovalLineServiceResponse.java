package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.ApproveStatus;
import org.springframework.lang.Nullable;

public record ApprovalLineServiceResponse(
        Long approvalLinePk,
        Integer approvalOrder,
        String approvalId,
        ApproveStatus approveStatus,
        boolean finalApproval,
        @Nullable
        Long vacationId
        ) {
}
