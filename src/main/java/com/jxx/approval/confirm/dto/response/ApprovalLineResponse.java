package com.jxx.approval.confirm.dto.response;

import java.util.List;

public record ApprovalLineResponse(
        ConfirmDocumentServiceDto confirmDocument,
        List<ApprovalLineServiceDto> approvalLines
) {
}
