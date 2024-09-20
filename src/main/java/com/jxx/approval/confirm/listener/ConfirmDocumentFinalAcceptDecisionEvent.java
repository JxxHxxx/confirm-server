package com.jxx.approval.confirm.listener;

import com.jxx.approval.confirm.domain.document.DocumentType;

import java.time.LocalDateTime;

/** 결재 문서 최종 승인 시 발생하는 이벤트 **/
public record ConfirmDocumentFinalAcceptDecisionEvent(
        String confirmDocumentId,
        String companyId,
        DocumentType documentType,
        LocalDateTime completedTime
) {
}
