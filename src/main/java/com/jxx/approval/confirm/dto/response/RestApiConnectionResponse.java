package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.document.DocumentType;

import java.time.LocalDateTime;

public record RestApiConnectionResponse(

        Long connectionPk,
        String scheme,
        String host,
        int port,
        String methodType,
        String path,
        String triggerType,
        DocumentType documentType,
        LocalDateTime createDateTime,
        String requesterId) {
}
