package com.jxx.approval.confirm.application;

public record ConfirmDocumentContentRequest(
        String elementKey,
        String elementName,
        String elementValue
) {
}
