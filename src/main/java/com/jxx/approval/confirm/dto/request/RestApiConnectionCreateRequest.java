package com.jxx.approval.confirm.dto.request;

import com.jxx.approval.confirm.domain.document.DocumentType;

import java.util.List;

/** Admin **/
public record RestApiConnectionCreateRequest(
    String description,
    DocumentType documentType,
    String host,
    String methodType,
    String path,
    String scheme,
    String triggerType,
    List<ConnectionElementCreateRequest> connectionElements
) {
}
