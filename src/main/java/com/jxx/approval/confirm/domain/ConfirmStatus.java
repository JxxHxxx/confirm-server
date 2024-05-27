package com.jxx.approval.confirm.domain;

import lombok.Getter;

import java.util.List;


// 선결 - 먼저 결재 가능, 후결, 대결, 전결
@Getter
public enum ConfirmStatus {
    CREATE("결재 생성"), // 200
    UPDATE("결재 수정"), // 210
    RAISE("결재 상신"),  // 300
    ACCEPT("결재 승인"), // 400
    REJECT("결재 반려"), // 220
    CANCEL("결재 취소"); // 100

    private final String description;

    protected static final List<ConfirmStatus> cancelPossible = List.of(CREATE, UPDATE, REJECT);
    protected static final List<ConfirmStatus> raisePossible = List.of(CREATE, UPDATE, REJECT);
    protected static final List<ConfirmStatus> raiseBefore = List.of(CREATE, UPDATE);

    public static final List<ConfirmStatus> rejectPossibleOfApproval = List.of(RAISE);
    public static final List<ConfirmStatus> acceptPossibleOfApproval = List.of(RAISE);

    ConfirmStatus(String description) {
        this.description = description;
    }
}
