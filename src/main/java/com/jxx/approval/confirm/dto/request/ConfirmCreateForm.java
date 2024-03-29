package com.jxx.approval.confirm.dto.request;

import com.jxx.approval.confirm.domain.DocumentType;

public record ConfirmCreateForm(
        String companyId,
        String departmentId,
        String createSystem,
        DocumentType documentType,
        String requesterId
) {
}
