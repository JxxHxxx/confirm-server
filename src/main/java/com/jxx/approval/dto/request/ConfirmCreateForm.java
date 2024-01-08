package com.jxx.approval.dto.request;

import com.jxx.approval.domain.ConfirmStatus;
import com.jxx.approval.domain.DocumentType;

public record ConfirmCreateForm(
        String confirmDocument,
        String companyId,
        String departmentId,
        String createSystem,
        ConfirmStatus confirmStatus,
        DocumentType documentType,
        String requesterId
) {
}
