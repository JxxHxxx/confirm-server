package com.jxx.approval.dto.request;

public record ConfirmRaiseForm(
        String companyId,
        String departmentId,
        String requesterId
) {
}
