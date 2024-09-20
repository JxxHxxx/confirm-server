package com.jxx.approval.confirm.listener;

import com.jxx.approval.confirm.domain.document.ConfirmStatus;
import com.jxx.approval.confirm.domain.document.DocumentType;

import java.time.LocalDateTime;

/** 결재 문서 반려 시 발생하는 이벤트 **/
public record ConfirmDocumentRejectDecisionEvent(
        String confirmDocumentId,
        DocumentType documentType,
        String companyId,
        LocalDateTime completedTime
) {
}
