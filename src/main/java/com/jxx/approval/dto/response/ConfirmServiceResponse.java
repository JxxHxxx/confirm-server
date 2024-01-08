package com.jxx.approval.dto.response;

public record ConfirmServiceResponse(
        Long confirmDocumentPk,
        String requesterId
) {
}
