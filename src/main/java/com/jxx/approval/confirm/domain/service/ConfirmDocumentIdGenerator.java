package com.jxx.approval.confirm.domain.service;


import com.jxx.approval.confirm.dto.request.ConfirmCreateForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfirmDocumentIdGenerator {
    private static final String SYSTEM_ID = "CON";
    public static String generate(ConfirmCreateForm form) {
        StringBuilder builder = new StringBuilder();
        String confirmDocumentId = builder
                .append(form.documentType().name())
                .append(form.companyId())
                .append(form.resourceId())
                .toString();

        log.info("\n[CREATE ConfirmDocumentId:{}]", confirmDocumentId);

        return confirmDocumentId;
    }
}
