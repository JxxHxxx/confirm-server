package com.jxx.approval.confirm.domain;

import lombok.Getter;

@Getter
public enum ApproveStatus {
    PENDING("대기"),
    ACCEPT("승인"),
    REJECT("반려"),
    FORWARD("전결"), // 최종결재자 전에 최종 결정을 함
    SUBSEQUENT("후결"), // 결재 승인 전에 일을 처리 (반복적인 업무 처리)
    DELEGATE("대결"); // 결재 위임


    private final String description;

    ApproveStatus(String description) {
        this.description = description;
    }
}