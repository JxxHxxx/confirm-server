package com.jxx.approval.dto.request;

public record ConfirmDocumentCancelForm(
        String companyId,
        String departmentId,
        String requesterId
) {
}
