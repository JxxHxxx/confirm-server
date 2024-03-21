package com.jxx.approval.confirm.listener;

import com.jxx.approval.confirm.domain.ConfirmStatus;

public record ConfirmStatusEvent(
        Long confirmDocumentPk,
        ConfirmStatus confirmStatus
) {
}
