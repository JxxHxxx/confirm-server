package com.jxx.approval.confirm.dto.request;

import java.util.List;

public record FormElementCreateRequest(
        List<Long> elementPks,
        String formId,
        String formName,
        String companyId

) {
}
