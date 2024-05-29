package com.jxx.approval.confirm.domain.line;


import lombok.Builder;

import java.util.List;

import static com.jxx.approval.confirm.domain.line.ApprovalLineException.*;

public class ApprovalLineManager {

    private final List<ApprovalLine> approvalLines;
    private final String approvalLineId;
    private ApprovalLine approvalLine;
    private boolean isEmptyApprovalLineFlag;
    private boolean checkBelongInApprovalLineFlag;
    private boolean checkApprovalOrderLineFlag;

    @Builder
    public ApprovalLineManager(List<ApprovalLine> approvalLines, String approvalLineId) {
        this.approvalLines = approvalLines;
        this.approvalLineId = approvalLineId;

        checkBelongInApprovalLineFlag = false;
        checkApprovalOrderLineFlag = false;
        isEmptyApprovalLineFlag = false;
    }
    
    public ApprovalLineManager isEmptyApprovalLine() {
        if (approvalLines.isEmpty()) {
            throw new ApprovalLineException(EMPTY_APPROVAL_LINE);
        }
        isEmptyApprovalLineFlag = true;
        return this;
    }

    // 해당 결재 문서 권한이 있는지 검증
    public ApprovalLineManager checkBelongInApprovalLine() {
        if (!isEmptyApprovalLineFlag) {
            throw new IllegalOrderMethodInvokeException("isEmptyApprovalLineFlag : " + isEmptyApprovalLineFlag);
        }
        ApprovalLine approvalLine = approvalLines.stream()
                .filter(al -> al.matchApprovalLineId(approvalLineId))
                .findFirst()
                .orElseThrow(() -> new ApprovalLineException("해당 문서에 대한결재 권한이 없습니다.", approvalLineId));

        this.approvalLine = approvalLine;
        checkBelongInApprovalLineFlag = true;
        return this;
    }

    // 자신의 결재 순서인지 검증
    public ApprovalLineManager checkApprovalLineOrder() {
        if (!isEmptyApprovalLineFlag || !checkBelongInApprovalLineFlag) {
            throw new IllegalOrderMethodInvokeException(
                    "isEmptyApprovalLineFlag : " + isEmptyApprovalLineFlag +
                    " checkBelongInApprovalLineFlag : " + checkBelongInApprovalLineFlag);
        }

        if (previousOrderApprovalLine(ApproveStatus.PENDING)) {
            throw new ApprovalLineException("당신의 차례가 아닙니다.", approvalLineId);
        }

        if (previousOrderApprovalLine(ApproveStatus.REJECT)) {
            throw new ApprovalLineException("반려된 결재 문서입니다.", approvalLineId);
        }

        checkApprovalOrderLineFlag = true;
        return this;
    }

    public ApprovalLine changeApproveStatus(ApproveStatus approveStatus) {
        if (!isEmptyApprovalLineFlag || !checkBelongInApprovalLineFlag || !checkApprovalOrderLineFlag) {
            throw new IllegalOrderMethodInvokeException(
                    "isEmptyApprovalLineFlag : " + isEmptyApprovalLineFlag +
                    "checkBelongInApprovalLineFlag : " + checkBelongInApprovalLineFlag +
                    "checkApprovalOrderLineFlag : "  + checkApprovalOrderLineFlag);
        }

        approvalLine.changeApproveStatus(approveStatus);
        return approvalLine;
    }

    private boolean previousOrderApprovalLine(ApproveStatus approveStatus) {
        return approvalLines.stream()
                .filter(al -> al.isBeforeOrderThan(approvalLine)) // 이전 결재자 필터링
                .anyMatch(al -> al.isApproveStatus(approveStatus));
    }

    protected List<ApprovalLine> getApprovalLines() {
        return approvalLines;
    }

    protected String getApprovalLineId() {
        return approvalLineId;
    }

    protected ApprovalLine getApprovalLine() {
        return approvalLine;
    }

    protected boolean isEmptyApprovalLineFlag() {
        return isEmptyApprovalLineFlag;
    }

    protected boolean isCheckBelongInApprovalLineFlag() {
        return checkBelongInApprovalLineFlag;
    }

    protected boolean isCheckApprovalOrderLineFlag() {
        return checkApprovalOrderLineFlag;
    }
}
