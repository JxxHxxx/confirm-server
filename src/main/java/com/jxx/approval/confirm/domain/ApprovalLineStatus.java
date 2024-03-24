package com.jxx.approval.confirm.domain;

public enum ApprovalLineStatus {

    BEFORE_CREATE("결재선생성전"),
    CREATED("결재선생성"),
    PROCESS_MODIFIABLE("결재진행중:변경가능"),
    PROCESS_UNMODIFIABLE("결재진행중:변경불가능");

    private final String description;

    ApprovalLineStatus(String description) {
        this.description = description;
    }
}
