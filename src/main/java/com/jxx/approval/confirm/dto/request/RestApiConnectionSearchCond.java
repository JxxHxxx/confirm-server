package com.jxx.approval.confirm.dto.request;

public record RestApiConnectionSearchCond(
        String triggerType,
        String documentType,
        String requesterId,
        Boolean used

) {
}
