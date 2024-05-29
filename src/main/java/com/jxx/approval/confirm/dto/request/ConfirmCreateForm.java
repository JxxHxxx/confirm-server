package com.jxx.approval.confirm.dto.request;

import com.jxx.approval.confirm.domain.document.DocumentType;

public record ConfirmCreateForm(
        String companyId,
        String departmentId,
        String departmentName,
        String createSystem,
        DocumentType documentType,
        String requesterId,
        String requesterName
) {
}
