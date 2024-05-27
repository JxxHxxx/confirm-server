package com.jxx.approval.confirm.dto.request;

public record ConfirmDocumentFormRequest(
        String formId,
        String formName,
        String companyId
) {
}
