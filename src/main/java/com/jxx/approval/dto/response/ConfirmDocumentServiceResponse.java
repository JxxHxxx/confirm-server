package com.jxx.approval.dto.response;

import com.jxx.approval.domain.ConfirmStatus;
import com.jxx.approval.domain.DocumentType;

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
    public ConfirmReadAllResponse confirmDocumentReadAllResponse() {
        return new ConfirmReadAllResponse(pk, confirmDocumentId, companyId, departmentId, createSystem, confirmStatus, documentType, requesterId);
    }
}
