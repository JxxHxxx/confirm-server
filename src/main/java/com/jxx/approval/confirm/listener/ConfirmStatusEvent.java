package com.jxx.approval.confirm.listener;

import com.jxx.approval.confirm.domain.ConfirmStatus;

public record ConfirmStatusEvent(
        Long confirmDocumentPk,
        String vacationId, // confirmDocumentId 를 분해하거나 Vacation 서버에 confirmDocumentId 만들어두는게 좋을듯.
        ConfirmStatus confirmStatusToChange // 해당 상태값으로 변경
) {
}
