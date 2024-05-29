package com.jxx.approval.confirm.domain.line;

public class ApprovalLineException extends RuntimeException {

    private String approvalId;

    public ApprovalLineException(String message) {
        super(message);
    }

    public ApprovalLineException(String message, String approvalId) {
        super(message);
        this.approvalId = approvalId;
    }

    public static final String EMPTY_APPROVAL_LINE = "빈 결재라인 입니다.";

    public String getApprovalId() {
        return approvalId;
    }
}
