package com.jxx.approval.confirm.domain.document;

import lombok.Getter;

@Getter
public class ConfirmDocumentException extends RuntimeException {

    private static final String AMBIGUOUS_REQUESTER_ID_VALUE = "";
    private String requesterId;
    private String errorCode;
    public ConfirmDocumentException(String message, String requesterId) {
        super(message);
        this.requesterId = requesterId;
    }

    public ConfirmDocumentException(String message, String requesterId, String errorCode) {
        super(message);
        this.requesterId = requesterId;
        this.errorCode = errorCode;
    }

    public ConfirmDocumentException(String message) {
        super(message);
        requesterId = AMBIGUOUS_REQUESTER_ID_VALUE;
    }

    public static final String FAIL_SELF_VERIFICATION = "자기 인증 실패";
    public static final String FAIL_RAISE = "결재 실패";
    public static final String FAIL_CANCEL = "취소 실패";


}
