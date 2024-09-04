package com.jxx.approval.confirm.domain.service;


import com.jxx.approval.confirm.dto.request.ConfirmCreateForm;

public class ConfirmDocumentIdGenerator {
    private static final String SYSTEM_ID = "CON";
    public static String generate(ConfirmCreateForm form) {
        StringBuilder builder = new StringBuilder();
        return builder
                .append(form.documentType().name())
                .append(form.companyId())
                .append(form.resourceId())
                .toString();
    }
}
