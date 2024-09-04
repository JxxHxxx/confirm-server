package com.jxx.approval.confirm.domain.document;

public enum DocumentType {
    VAC("VACATION", "휴가 신청"),
    DCR("DATA_CHANGE", "데이터 변경 요청"),
    COST("COST","지출내역서"),
    WRK("WORK_TICKET", "작업 요청");

    private final String fullName;
    private final String description;

    DocumentType(String fullName, String description) {
        this.fullName = fullName;
        this.description = description;
    }
}
