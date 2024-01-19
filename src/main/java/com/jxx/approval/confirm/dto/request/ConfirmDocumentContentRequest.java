package com.jxx.approval.confirm.dto.request;

public record ConfirmDocumentContentRequest(
        String elementKey,
        String elementName,
        String elementValue
) {
}
