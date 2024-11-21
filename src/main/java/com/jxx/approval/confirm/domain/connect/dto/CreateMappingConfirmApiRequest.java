package com.jxx.approval.confirm.domain.connect.dto;

import com.jxx.approval.confirm.domain.document.DocumentType;
public record CreateMappingConfirmApiRequest(

        String scheme,
        String host,
        int port,
        String methodType,
        String path,
        String requesterId,
        String description,
        String triggerType,
        DocumentType documentType
) {
}
