package com.jxx.approval.confirm.dto.request;

public record ConfirmRaiseForm(
        String companyId,
        String departmentId,
        String requesterId
) {
}
