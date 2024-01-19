package com.jxx.approval.confirm.dto.request;

public record ApprovalInformationForm(
        String companyId,
        String departmentId,
        String approvalLineId
) {
}
