package com.jxx.approval.confirm.listener;

import com.jxx.approval.confirm.domain.ConfirmStatus;

public record ConfirmStatusEvent(
        Long confirmDocumentPk,
        Long vacationId,
        ConfirmStatus confirmStatusToChange // 해당 상태값으로 변경
) {
}
