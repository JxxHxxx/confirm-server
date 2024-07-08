package com.jxx.approval.confirm.dto.request;

public record ConfirmDocumentSearchCondition(
        Long confirmDocumentPk,
        String confirmDocumentId,
        String companyId,
        String departmentId,
        String requesterId,
        String requesterName,
        String approvalId,
        String approvalName,
        String approveStatus
) {
}
