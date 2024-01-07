package com.jxx.approval.dto.response;

public record ConfirmDocumentServiceResponse(
        String confirmDocumentId,
        String approvalId,
        String requesterId
) {
}
