package com.jxx.approval.confirm.domain.service;


public class ConfirmDocumentIdGenerator {
    private static final String SYSTEM_ID = "CON";
    public static String generate(String companyId) {
        StringBuilder builder = new StringBuilder();
        return builder
                .append(SYSTEM_ID)
                .append(companyId)
                .append(System.currentTimeMillis())
                .toString();
    }
}
