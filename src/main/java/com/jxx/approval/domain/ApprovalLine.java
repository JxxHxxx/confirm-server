package com.jxx.approval.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_APPROVAL_LINE_MASTER")
public class ApprovalLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPROVER_LINE_PK")
    @Comment(value = "결재자 테이블 PK")
    private Long pk;
    @Column(name = "APPROVAL_LINE_ORDER", nullable = false)
    @Comment(value = "결재 순서")
    private Integer approvalOrder;
    @Column(name = "APPROVAL_LINE_ID", nullable = false)
    @Comment(value = "결재자 ID")
    private String approvalLineId;
    @Column(name = "APPROVAL_STATUS", nullable = false)
    @Comment(value = "결재 승인 여부")
    @Enumerated(EnumType.STRING)
    private ApproveStatus approveStatus;

    @Column(name = "APPROVAL_TIME", nullable = true)
    @Comment(value = "결재 승인/반려 일시")
    private LocalDateTime approveTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONFIRM_DOCUMENT_PK", referencedColumnName = "CONFIRM_DOCUMENT_PK", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment(value = "결재 문서 테이블 PK")
    private ConfirmDocument confirmDocument;

    public void setConfirmDocument(ConfirmDocument confirmDocument) {
        this.confirmDocument = confirmDocument;
        confirmDocument.getApprovalLines().add(this);
    }

    @Builder
    public ApprovalLine(Integer approvalOrder, String approvalLineId, ConfirmDocument confirmDocument) {
        this.approvalOrder = approvalOrder;
        this.approvalLineId = approvalLineId;
        this.approveTime = null;
        this.approveStatus = ApproveStatus.PENDING;
        this.confirmDocument = confirmDocument;
    }

    public boolean matchApprovalLineId(String approvalId) {
        return this.approvalLineId.equals(approvalId);
    }

    public void tryAccept() {
        if (!approveStatus.equals(ApproveStatus.PENDING)) {
            throw new IllegalArgumentException("이미 처리한 결재 문서입니다.");
        }
    }
    protected void accept() {
        approveStatus = ApproveStatus.ACCEPT;
    }

    protected void changeApproveStatus(ApproveStatus approveStatus) {
        this.approveStatus = approveStatus;
    }

    public boolean checkApproveStatus(ApproveStatus approveStatus) {
        return this.approveStatus.equals(approveStatus);
    }

    public boolean isBeforeOrderThan(ApprovalLine approvalLine) {
        return this.approvalOrder < approvalLine.getApprovalOrder();
    }
}
