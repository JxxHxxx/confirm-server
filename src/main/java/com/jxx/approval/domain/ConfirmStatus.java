package com.jxx.approval.domain;

import lombok.Getter;

@Getter
public enum ConfirmStatus {
    CREATE("결재 생성"), // 200
    UPDATE("결재 수정"), // 201
    RAISE("결재 상신"),  // 300
    ACCEPT("결재 승인"), // 400
    REJECT("결재 반려"), // 202
    CANCEL("결재 취소"); // 100

    private final String description;

    ConfirmStatus(String description) {
        this.description = description;
    }
}
