package com.jxx.approval.confirm.dto.response;

import java.util.Map;

public record ConfirmDocumentAndContentServiceResponse(
        ConfirmDocumentServiceResponse confirmDocument,
        Long contentPk,
        Map<String, Object> contents
) {
}
