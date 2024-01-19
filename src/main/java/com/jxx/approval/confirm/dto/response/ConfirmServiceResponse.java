package com.jxx.approval.confirm.dto.response;

public record ConfirmServiceResponse(
        Long confirmDocumentPk,
        String requesterId
) {
}
