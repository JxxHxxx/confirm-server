package com.jxx.approval.confirm.listener;

import com.jxx.approval.confirm.domain.document.ConfirmDocument;

/** 결재 문서 최종 승인 시 발생하는 이벤트 **/
public record ConfirmDocumentFinalAcceptDecisionEvent(
        ConfirmDocument confirmDocument,
        String triggerType
) {
}
