package com.jxx.approval.dto.request;

public record ApprovalInformationForm(
        String companyId,
        String departmentId,
        String approvalLineId
) {
}
