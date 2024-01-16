package com.jxx.approval.dto.request;

import java.util.List;

public record FormElementCreateRequest(
        List<Long> elementPks,
        String formId,
        String formName,
        String companyId

) {
}
