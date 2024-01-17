package com.jxx.approval.domain;


import lombok.Builder;

import java.util.List;

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
            throw new IllegalArgumentException("결재 라인이 아닙니다.");
        }
        isEmptyApprovalLineFlag = true;
        return this;
    }

    // 해당 결재 문서 권한이 있는지 검증
    public ApprovalLineManager checkBelongInApprovalLine() {
        if (!isEmptyApprovalLineFlag) {
            throw new IllegalArgumentException("afterPropertiesSetFlag() 를 호출하세요.");
        }
        ApprovalLine approvalLine = approvalLines.stream()
                .filter(al -> al.matchApprovalLineId(approvalLineId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 문서에 대한결재 권한이 없습니다."));

        this.approvalLine = approvalLine;
        checkBelongInApprovalLineFlag = true;
        return this;
    }

    // 자신의 결재 순서인지 검증
    public ApprovalLineManager checkApprovalLineOrder() {
        if (!isEmptyApprovalLineFlag || !checkBelongInApprovalLineFlag) {
            throw new IllegalArgumentException("filterRequesterInApprovalLine() 이 호출되지 않았습니다.");
        }

        if (previousOrderApprovalLine(ApproveStatus.PENDING)) {
            throw new IllegalArgumentException("당신의 차례가 아닙니다.");
        }

        if (previousOrderApprovalLine(ApproveStatus.REJECT)) {
            throw new IllegalArgumentException("반려된 결재 문서입니다.");
        }

        checkApprovalOrderLineFlag = true;
        return this;
    }

    public ApprovalLine changeApproveStatus(ApproveStatus approveStatus) {
        if (!isEmptyApprovalLineFlag || !checkBelongInApprovalLineFlag || !checkApprovalOrderLineFlag) {
            throw new IllegalArgumentException("checkRequestersOrderTurn() 이 호출되지 않았습니다.");
        }
        
        approvalLine.changeApproveStatus(approveStatus);
        return approvalLine;
    }

    private boolean previousOrderApprovalLine(ApproveStatus approveStatus) {
        return approvalLines.stream()
                .filter(al -> al.isBeforeOrderThan(approvalLine)) // 이전 결재자 필터링
                .anyMatch(al -> al.checkApproveStatus(approveStatus));
    }
}
