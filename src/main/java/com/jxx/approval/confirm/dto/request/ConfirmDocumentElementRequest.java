package com.jxx.approval.confirm.dto.request;

import java.util.List;

public record ConfirmDocumentElementRequest(
        String companyId,
        List<ElementPair> elementPairs
) {
}
