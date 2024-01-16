package com.jxx.approval.dto.response;

public record ConfirmDocumentElementServiceResponse(
        Long pk,
        String elementKey,
        String elementName
) {
}
