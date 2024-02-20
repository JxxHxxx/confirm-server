package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.ConfirmStatus;

public record ConfirmServiceResponseV2(
        String confirmDocumentId,
        String requesterId,
        ConfirmStatus confirmStatus) {
}
