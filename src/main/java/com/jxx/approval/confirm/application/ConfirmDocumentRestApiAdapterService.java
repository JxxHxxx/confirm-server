package com.jxx.approval.confirm.application;

import com.jxx.approval.confirm.domain.connect.RestApiConnResponseCode;
import com.jxx.approval.confirm.domain.document.ConfirmDocument;

public interface ConfirmDocumentRestApiAdapterService {

    RestApiConnResponseCode call(ConfirmDocument confirmDocument, String triggerType);
}
