package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.dto.request.ElementPair;

import java.util.List;

public record ConfirmDocumentElementResponse(

        ConfirmDocumentFormResponse form,
        List<ElementPair> elements
) {
}
