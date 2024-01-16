package com.jxx.approval.dto.request;

import java.util.List;

public record FormElementCreateRequest(
        List<Long> pks,
        String formId,
        String formName,
        String companyId

) {
}
