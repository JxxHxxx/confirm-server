package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.ConfirmStatus;
import com.jxx.approval.confirm.domain.DocumentType;

public record ConfirmDocumentServiceResponse(
        Long pk,
        String confirmDocumentId,
        String companyId,
        String departmentId,
        String createSystem,
        ConfirmStatus confirmStatus,
        DocumentType documentType,
        String requesterId
) {
}
