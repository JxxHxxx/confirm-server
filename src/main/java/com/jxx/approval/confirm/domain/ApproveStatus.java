package com.jxx.approval.confirm.domain;

import lombok.Getter;

@Getter
public enum ApproveStatus {
    PENDING("대기"),
    ACCEPT("승인"),
    REJECT("반려");

    private final String description;

    ApproveStatus(String description) {
        this.description = description;
    }
}
