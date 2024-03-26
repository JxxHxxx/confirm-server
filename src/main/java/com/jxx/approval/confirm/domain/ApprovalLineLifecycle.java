package com.jxx.approval.confirm.domain;

public enum ApprovalLineLifecycle {

    BEFORE_CREATE("결재선 생성 전"),
    CREATED("결재선 생성 완료 "),
    PROCESS_MODIFIABLE("결재 진행중:변경 가능"),
    PROCESS_UNMODIFIABLE("결재 진행중:변경 불가능");

    private final String description;

    ApprovalLineLifecycle(String description) {
        this.description = description;
    }
}
