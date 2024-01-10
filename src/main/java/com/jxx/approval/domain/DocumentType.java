package com.jxx.approval.domain;

public enum DocumentType {
    VAC("VACATION", "휴가 신청"),
    DC("DATA_CHANGE", "데이터 변경 요청");

    private final String fullName;
    private final String description;

    DocumentType(String fullName, String description) {
        this.fullName = fullName;
        this.description = description;
    }
}
