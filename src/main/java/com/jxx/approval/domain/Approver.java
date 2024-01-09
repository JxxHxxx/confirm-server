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
@Table(name = "JXX_APPROVER_MASTER")
public class Approver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPROVER_PK")
    @Comment(value = "결재자 테이블 PK")
    private Long pk;
    @Column(name = "APPROVAL_ORDER", nullable = false)
    @Comment(value = "결재 순서")
    private Integer approvalOrder;
    @Column(name = "APPROVAL_ID", nullable = false)
    @Comment(value = "결재자 ID")
    private String approvalId;
    @Column(name = "APPROVE_STATUS", nullable = false)
    @Comment(value = "결재 승인 여부")
    @Enumerated(EnumType.STRING)
    private ApproveStatus approveStatus;

    @Column(name = "APPROVE_TIME", nullable = true)
    @Comment(value = "결재 승인/반려 일시")
    private LocalDateTime approveTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONFIRM_DOCUMENT_PK", referencedColumnName = "CONFIRM_DOCUMENT_PK", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment(value = "결재 문서 테이블 PK")
    private ConfirmDocument confirmDocument;

    public void setConfirmDocument(ConfirmDocument confirmDocument) {
        this.confirmDocument = confirmDocument;
        confirmDocument.getApprovers().add(this);
    }

    @Builder
    public Approver(Integer approvalOrder, String approvalId, ConfirmDocument confirmDocument) {
        this.approvalOrder = approvalOrder;
        this.approvalId = approvalId;
        this.approveTime = null;
        this.approveStatus = ApproveStatus.PENDING;
        this.confirmDocument = confirmDocument;
    }
}
