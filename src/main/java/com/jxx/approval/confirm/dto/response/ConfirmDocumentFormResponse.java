package com.jxx.approval.confirm.dto.response;

public record ConfirmDocumentFormResponse(
        Long confirmDocumentFormPk,
        String companyId,
        String confirmDocumentFormId,
        String confirmDocumentFormName
) {
}
