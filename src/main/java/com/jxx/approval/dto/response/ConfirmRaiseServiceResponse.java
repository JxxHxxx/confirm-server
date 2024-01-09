package com.jxx.approval.dto.response;

import com.jxx.approval.domain.ConfirmStatus;

public record ConfirmRaiseServiceResponse(
        Long confirmDocumentPk,
        String requesterId,
        ConfirmStatus confirmStatus
) {
}
