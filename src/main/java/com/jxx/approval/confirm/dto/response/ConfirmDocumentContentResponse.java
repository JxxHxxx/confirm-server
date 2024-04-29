package com.jxx.approval.confirm.dto.response;

import java.util.Map;

public record ConfirmDocumentContentResponse(
        Long contentPk,
        Map<String, Object> contents
) {
}
