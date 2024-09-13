package com.jxx.approval.confirm.domain.line;

import com.jxx.approval.confirm.domain.document.ConfirmDocument;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_APPROVAL_LINE_MASTER",
        indexes = {
                @Index(name = "IDX_APPROVAL_ID", columnList = "APPROVAL_ID", unique = false),
                @Index(name = "IDX_CONFIRM_DOCUMENT_ID_LGFK", columnList = "CONFIRM_DOCUMENT_ID", unique = false)
        })
public class ApprovalLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPROVAL_LINE_PK")
    @Comment(value = "결재자 테이블 PK")
    private Long pk;
    @Column(name = "APPROVAL_LINE_ORDER", nullable = false)
    @Comment(value = "결재 순서")
    private Integer approvalOrder;
    @Column(name = "APPROVAL_ID", nullable = false)
    @Comment(value = "결재자 ID")
    private String approvalLineId;
    @Column(name = "APPROVAL_NAME", nullable = false)
    @Comment(value = "결재자 이름")
    private String approvalName;
    @Column(name = "APPROVAL_DEPARTMENT_ID", nullable = false)
    @Comment(value = "결재자 부서 ID")
    private String approvalDepartmentId;
    @Column(name = "APPROVAL_DEPARTMENT_NAME", nullable = false)
    @Comment(value = "결재자 부서 명")
    private String approvalDepartmentName;

    @Column(name = "APPROVAL_STATUS", nullable = false)
    @Comment(value = "결재 승인 여부")
    @Enumerated(EnumType.STRING)
    private ApproveStatus approveStatus;

    @Column(name = "APPROVAL_TIME", nullable = true)
    @Comment(value = "결재 승인/반려 일시")
    private LocalDateTime approveTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONFIRM_DOCUMENT_ID", referencedColumnName = "CONFIRM_DOCUMENT_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment(value = "결재 문서 테이블 ID")
    private ConfirmDocument confirmDocument;

    public void setConfirmDocument(ConfirmDocument confirmDocument) {
        this.confirmDocument = confirmDocument;
        confirmDocument.getApprovalLines().add(this);
    }

    @Builder
    public ApprovalLine(Integer approvalOrder, String approvalLineId, String approvalName, String approvalDepartmentId,
                        String approvalDepartmentName, ConfirmDocument confirmDocument) {
        this.approvalOrder = approvalOrder;
        this.approvalLineId = approvalLineId;
        this.approvalName = approvalName;
        this.approvalDepartmentId = approvalDepartmentId;
        this.approvalDepartmentName = approvalDepartmentName;
        this.approveTime = null;
        this.approveStatus = ApproveStatus.PENDING;
        this.confirmDocument = confirmDocument;
    }

    public boolean matchApprovalLineId(String approvalId) {
        return this.approvalLineId.equals(approvalId);
    }

    public void changeApproveStatus(ApproveStatus approveStatus) {
        this.approveStatus = approveStatus;
        approveTime = LocalDateTime.now();
    }

    public boolean isApproveStatus(ApproveStatus approveStatus) {
        return this.approveStatus.equals(approveStatus);
    }

    public boolean isNotApproveStatus(ApproveStatus approveStatus) {
        return !this.approveStatus.equals(approveStatus);
    }

    public boolean isBeforeOrderThan(ApprovalLine approvalLine) {
        return this.approvalOrder < approvalLine.getApprovalOrder();
    }

    public boolean isFinalApproval(List<ApprovalLine> approvalLines) {
        return this.approvalOrder.equals(approvalLines.size());
    }
}
