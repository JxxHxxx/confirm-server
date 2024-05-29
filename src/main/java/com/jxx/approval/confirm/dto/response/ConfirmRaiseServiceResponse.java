package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.document.ConfirmStatus;

public record ConfirmRaiseServiceResponse(
        Long confirmDocumentPk,
        String requesterId,
        ConfirmStatus confirmStatus
) {
}
