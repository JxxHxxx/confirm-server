package com.jxx.approval.confirm.dto.request;

import com.jxx.approval.confirm.domain.document.DocumentType;

import java.util.Map;

public record ConfirmCreateForm(

        Long resourceId, // third-party 식별자
        String companyId,
        String departmentId,
        String departmentName,
        String createSystem,
        DocumentType documentType,
        String requesterId,
        String requesterName,
        Map<String, Object> contents
) {
}
