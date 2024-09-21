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

    public static final String EMPTY_APPROVAL_LINE = "결재라인이 비어있습니다. 결재선을 지정해주세요";

    public String getApprovalId() {
        return approvalId;
    }
}
