package com.jxx.approval.confirm.dto.request;

import com.jxx.approval.confirm.domain.document.DocumentType;

public record ConfirmConnectionApiRequest(

        String scheme,
        String host,
        Integer port,
        String methodType,
        String path,
        String triggerType,
        DocumentType documentType,
        String description,
        String requesterId

        ) {
}
