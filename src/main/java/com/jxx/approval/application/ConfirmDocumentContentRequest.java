package com.jxx.approval.application;

public record ConfirmDocumentContentRequest(
        String elementKey,
        String elementName,
        String elementValue
) {
}
