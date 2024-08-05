package com.jxx.approval.confirm.dto.request;


import java.util.List;

public record ConfirmDocumentSearchCondition(
        Long confirmDocumentPk,
        String startDate,
        String endDate,
        String confirmDocumentId,
        String companyId,
        String departmentId,
        String requesterId,
        String requesterName,
        String approvalId,
        String approvalName,
        List<String> confirmStatus,
        String approveStatus
) {
}
