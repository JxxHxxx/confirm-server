package com.jxx.approval.confirm.dto.response;

public record ConfirmDocumentElementServiceResponse(
        Long pk,
        String elementKey,
        String elementName
) {
}
