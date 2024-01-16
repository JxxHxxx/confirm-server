package com.jxx.approval.domain;

import lombok.Getter;

@Getter
public class ConfirmDocumentException extends  RuntimeException {

    private final String requesterId;
    public ConfirmDocumentException(String message, String requesterId) {
        super(message);
        this.requesterId = requesterId;
    }

    public static final String FAIL_SELF_VERIFICATION = "자기 인증 실패";
    public static final String FAIL_RAISE = "결재 실패";
    public static final String FAIL_CANCEL = "취소 실패";


}
