package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.ApproveStatus;

import java.time.LocalDateTime;

public record ApprovalLineServiceDto(

        Long approvalLinePk,
        Integer approvalOrder,
        String approvalId,
        ApproveStatus approveStatus,
        String approvalName,
        LocalDateTime approveTime
) {
}
