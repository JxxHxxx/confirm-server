package com.jxx.approval.confirm.dto.response;

import java.time.LocalDateTime;

public record ConfirmDocumentFormResponse(
        Long confirmDocumentFormPk,
        String companyId,
        String confirmDocumentFormId,
        String confirmDocumentFormName,
        boolean used,
        LocalDateTime createTime
) {
}
