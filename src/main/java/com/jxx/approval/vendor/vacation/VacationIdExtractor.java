package com.jxx.approval.vendor.vacation;

import com.jxx.approval.confirm.domain.ConfirmDocument;

public class VacationIdExtractor {

    private static final String WHITE_SPACE = "";
    public static Long execute(ConfirmDocument confirmDocument) {
        String confirmDocumentId = confirmDocument.getConfirmDocumentId();
        String vacationId = confirmDocumentId
                .replaceFirst(confirmDocument.documentTypeString(), WHITE_SPACE)
                .replaceFirst(confirmDocument.getCompanyId(), WHITE_SPACE);

        return Long.valueOf(vacationId);
    }
}
