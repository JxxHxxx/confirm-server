package com.jxx.approval.confirm.application;

import com.jxx.approval.confirm.domain.document.ConfirmDocument;

public interface ConfirmDocumentRestApiAdapterService {

    boolean call(ConfirmDocument confirmDocument, String triggerType);
}
