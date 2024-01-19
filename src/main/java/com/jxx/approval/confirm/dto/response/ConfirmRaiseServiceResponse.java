package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.ConfirmStatus;

public record ConfirmRaiseServiceResponse(
        Long confirmDocumentPk,
        String requesterId,
        ConfirmStatus confirmStatus
) {
}
