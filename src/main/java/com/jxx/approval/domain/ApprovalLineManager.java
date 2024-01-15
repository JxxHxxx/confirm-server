package com.jxx.approval.domain;


import lombok.Builder;

import java.util.List;

public class ApprovalLineManager {

    private final List<ApprovalLine> approvalLines;
    private final String approvalLineId;
    private ApprovalLine approvalLine;
    
    private boolean afterPropertiesSetFlag;
    private boolean checkBelongInApprovalLineFlag;
    private boolean checkApprovalOrderLineFlag;

    @Builder
    public ApprovalLineManager(List<ApprovalLine> approvalLines, String approvalLineId) {
        this.approvalLines = approvalLines;
        this.approvalLineId = approvalLineId;

        checkBelongInApprovalLineFlag = false;
        checkApprovalOrderLineFlag = false;
        afterPropertiesSetFlag = false;
    }
    
    public ApprovalLineManager afterPropertiesSet() {
        if (approvalLines.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않은 결재 문서입니다.");
        }
        afterPropertiesSetFlag = true;
        return this;
    }


    // 해당 결재 문서 결재 권한이 있는지 여부
    public ApprovalLineManager checkBelongInApprovalLine() {
        if (!afterPropertiesSetFlag) {
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

    // 이전 결재자가 결재를 했는지 여부
    public ApprovalLineManager checkApprovalOrderLine() {
        if (!afterPropertiesSetFlag || !checkBelongInApprovalLineFlag) {
            throw new IllegalArgumentException("filterRequesterInApprovalLine() 이 호출되지 않았습니다.");
        }

        if (approvalLine == null) {
            throw new IllegalArgumentException("결재 라인이 없다.");
        }

        //이전 결재자가 결재를 했는지 여부
        boolean previousApprovalLineTurn = approvalLines.stream()
                .filter(al -> al.getApprovalOrder() < approvalLine.getApprovalOrder()) // 이전 결재자 필터링
                .anyMatch(al -> al.getApproveStatus().equals(ApproveStatus.PENDING));// 아직 승인/반려를 내리지 않은 이전 결재자가 존재하는지 체

        if (previousApprovalLineTurn) {
            throw new IllegalArgumentException("당신의 차례가 아닙니다.");
        }

        return this;
    }
    
    public ApprovalLine acceptApprovalLine() {
        if (!afterPropertiesSetFlag || !checkBelongInApprovalLineFlag || !checkApprovalOrderLineFlag) {
            throw new IllegalArgumentException("checkRequestersOrderTurn() 이 호출되지 않았습니다.");
        }
        
        approvalLine.accept();
        return approvalLine;
    }
}
