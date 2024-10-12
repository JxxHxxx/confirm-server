package com.jxx.approval.confirm.listener;

import com.jxx.approval.confirm.domain.document.ConfirmDocument;

public record ConfirmDocumentRaiseEvent(
        ConfirmDocument confirmDocument,
        String triggerType
) {
}
