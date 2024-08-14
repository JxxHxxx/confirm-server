package com.jxx.approval.confirm.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ConfirmDocumentFormElementResponse {
    private final String elementGroupKey;
    private final String elementGroupName;
    private final String elementGroupType;
    private final int elementGroupOrder;
    private List<ElementPairV2> elements;

    public void setElements(List<ElementPairV2> elements) {
          this.elements = elements;
    }
}
