package com.jxx.approval.confirm.dto.request;

import com.jxx.approval.confirm.dto.response.ElementPairV2;

import java.util.List;

public record ConfirmDocumentElementRequest(
        String companyId,
        String elementGroupKey,
        String elementGroupName,
        String elementGroupType,
        int elementGroupOrder,
        List<ElementPairV2> elementPairs
) {
}
