package com.jxx.approval.confirm.dto.request;

public record ApprovalLineEnrollForm(
        String approvalId,
        String approvalName,
        String approvalDepartmentId,
        String approvalDepartmentName,
        Integer approvalOrder
) {
}
