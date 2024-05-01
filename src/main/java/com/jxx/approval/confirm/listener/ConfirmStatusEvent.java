package com.jxx.approval.confirm.listener;

import com.jxx.approval.confirm.domain.ConfirmStatus;

public record ConfirmStatusEvent(
        String confirmDocumentId,
        Long vacationId,
        ConfirmStatus confirmStatusToChange // 해당 상태값으로 변경
) {
}
