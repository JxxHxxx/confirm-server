package com.jxx.approval.confirm.dto.request;

public record ConfirmDocumentCancelForm(
        String companyId,
        String departmentId,
        String requesterId
) {
}
