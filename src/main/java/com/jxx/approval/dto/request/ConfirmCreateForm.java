package com.jxx.approval.dto.request;

import com.jxx.approval.domain.DocumentType;

public record ConfirmCreateForm(
        String confirmDocumentId,
        String companyId,
        String departmentId,
        String createSystem,
        DocumentType documentType,
        String requesterId
) {
}
