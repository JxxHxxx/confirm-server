package com.jxx.approval.confirm.dto.request;

import com.jxx.approval.confirm.domain.connect.ElementType;
import com.jxx.approval.confirm.domain.connect.ElementValueType;

public record ConnectionElementCreateRequest(
        ElementType elementType,
        String elementKey,
        String elementValue,
        ElementValueType elementValueType
) {
}
