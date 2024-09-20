package com.jxx.approval.vendor.vacation;

import com.jxx.approval.confirm.domain.document.ConfirmDocument;
import com.jxx.approval.confirm.domain.document.DocumentType;

/**  **/
public class ResourceIdExtractor {

    private static final String WHITE_SPACE = "";
    public static Long execute(ConfirmDocument confirmDocument) {
        String confirmDocumentId = confirmDocument.getConfirmDocumentId();
        String vacationId = confirmDocumentId
                .replaceFirst(confirmDocument.documentTypeString(), WHITE_SPACE)
                .replaceFirst(confirmDocument.getCompanyId(), WHITE_SPACE);

        return Long.valueOf(vacationId);
    }

    public static Long execute(String confirmDocumentId, DocumentType documentType, String companyId) {
        String vacationId = confirmDocumentId
                .replaceFirst(documentType.name(), WHITE_SPACE)
                .replaceFirst(companyId, WHITE_SPACE);

        return Long.valueOf(vacationId);
    }
}
