package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.ConfirmStatus;

public record ConfirmDocumentServiceDto(
        String confirmDocumentId,
        String requesterId,
        ConfirmStatus confirmStatus) {
}
