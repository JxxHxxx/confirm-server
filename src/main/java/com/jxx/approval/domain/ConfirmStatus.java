package com.jxx.approval.domain;

import lombok.Getter;

@Getter
public enum ConfirmStatus {
    CREATE("결재 생성"),
    UPDATE("결재 수정"),
    RAISE("결재 상신"),
    ACCEPT("결재 승인"),
    REJECT("결재 반려"),
    CANCEL("결재 취소");

    private final String description;

    ConfirmStatus(String description) {
        this.description = description;
    }
}
