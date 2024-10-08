package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.document.DocumentType;
import com.jxx.approval.confirm.domain.line.ApproveStatus;
import org.springframework.lang.Nullable;

public record ApprovalLineServiceResponse(
        Long approvalLinePk,
        Integer approvalOrder,
        String approvalId,
        ApproveStatus approveStatus,
        boolean finalApproval,
        DocumentType documentType,
        String companyId,
        @Nullable
        String confirmDocumentId
        ) {
}
