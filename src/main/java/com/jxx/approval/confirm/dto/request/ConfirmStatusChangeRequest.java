package com.jxx.approval.confirm.dto.request;

public record ConfirmStatusChangeRequest(
        String requestSystem,
        String vacationStatus
) {
}
