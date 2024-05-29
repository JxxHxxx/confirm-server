package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.document.ConfirmStatus;
import com.jxx.approval.confirm.domain.document.DocumentType;
import lombok.Getter;

@Getter
public class ConfirmReadAllResponse {
    private final Long pk;
    private final String confirmDocumentId;
    private final String companyId;
    private final String departmentId;
    private final String createSystem;
    private final ConfirmStatus confirmStatus;
    private final DocumentType documentType;
    private final String approvalId;

    public ConfirmReadAllResponse(Long pk, String confirmDocumentId, String companyId, String departmentId, String createSystem, ConfirmStatus confirmStatus, DocumentType documentType, String approvalId) {
        this.pk = pk;
        this.confirmDocumentId = confirmDocumentId;
        this.companyId = companyId;
        this.departmentId = departmentId;
        this.createSystem = createSystem;
        this.confirmStatus = confirmStatus;
        this.documentType = documentType;
        this.approvalId = approvalId;
    }
}
