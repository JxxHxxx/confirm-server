package com.jxx.approval.domain;

public class ApprovalLineException extends RuntimeException {

    private final String approvalId;

    public ApprovalLineException(String message, String approvalId) {
        super(message);
        this.approvalId = approvalId;
    }

    public static final String EMPTY_APPROVAL_LINE = "빈 결재라인 입니다.";

    public String getApprovalId() {
        return approvalId;
    }
}
