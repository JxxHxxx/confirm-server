package com.jxx.approval.confirm.domain.document;

import com.jxx.approval.confirm.domain.line.ApprovalLine;
import com.jxx.approval.confirm.domain.line.ApprovalLineLifecycle;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.jxx.approval.confirm.domain.line.ApproveStatus.ACCEPT;
import static com.jxx.approval.confirm.domain.document.ConfirmStatus.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_CONFIRM_DOCUMENT_MASTER",
        indexes = {
                @Index(name = "IDX_CONFIRM_DOCUMENT_ID", columnList = "CONFIRM_DOCUMENT_ID", unique = true),
                @Index(name = "IDX_REQUESTER_NAME", columnList = "REQUESTER_NAME", unique = false),
                @Index(name = "IDX_CPN_ORG_RID", columnList = "COMPANY_ID, DEPARTMENT_ID, REQUESTER_ID", unique = false)})
public class ConfirmDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIRM_DOCUMENT_PK")
    @Comment(value = "결재 문서 테이블 PK")
    private Long pk;
    @Column(name = "CONFIRM_DOCUMENT_ID", unique = true)
    @Comment(value = "결재 문서 ID")
    private String confirmDocumentId;
    @Embedded
    private Document document;
    @Embedded
    private Requester requester;
    @Column(name = "CONFIRM_STATUS", nullable = false)
    @Comment(value = "결재 상태")
    @Enumerated(EnumType.STRING)
    private ConfirmStatus confirmStatus;
    @Column(name = "CREATE_SYSTEM", nullable = false)
    @Comment(value = "결재 데이터를 생성한 시스템")
    private String createSystem;

    @Column(name = "CREATE_TIME", nullable = false)
    @Comment(value = "결재 문서 생성 시간")
    private LocalDateTime createTime;

    @OneToMany(mappedBy = "confirmDocument", fetch = FetchType.LAZY)
    private List<ApprovalLine> approvalLines = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "APPROVAL_LINE_LIFE_CYCLE")
    @Comment(value = "결재선 상태")
    private ApprovalLineLifecycle approvalLineLifecycle;
    // TODO 문서 최종 결정 시간 반려 or 최종 승인 필드
    @Column(name = "COMPLETED_TIME", nullable = true)
    @Comment(value = "결재 문서가 최종 승인/반려된 시간")
    private LocalDateTime completedTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONFIRM_DOCUMENT_CONTENT_PK", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment(value = "결재 문서 본문")
    private ConfirmDocumentContent content;

    @Builder
    public ConfirmDocument(String confirmDocumentId, Document document, Requester requester, ConfirmStatus confirmStatus, String createSystem,
                           ApprovalLineLifecycle approvalLineLifecycle) {
        this.confirmDocumentId = confirmDocumentId;
        this.document = document;
        this.requester = requester;
        this.confirmStatus = confirmStatus;
        this.createSystem = createSystem;
        this.approvalLineLifecycle = approvalLineLifecycle;
        this.createTime = LocalDateTime.now();
    }

    public void setConfirmDocumentId(String confirmDocumentId) {
        this.confirmDocumentId = confirmDocumentId;
    }

    public void setContent(ConfirmDocumentContent content) {
        this.content = content;
    }

    public void changeConfirmStatus(ConfirmStatus confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    private void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
    }

    private void setApprovalLineLifecycle(ApprovalLineLifecycle approvalLineLifecycle) {
        this.approvalLineLifecycle = approvalLineLifecycle;
    }

    /**
     * 종료 상태가 된 결재 문서 후속 처리
     * 결재 문서의 종료 상태란, 최종 결정권자의 승인, 중도 반려 등이 존재한다.
     **/
    public void processCompletedConfirmDocument(ConfirmStatus confirmStatus, LocalDateTime completedTime) {
        changeConfirmStatus(confirmStatus);
        setCompletedTime(completedTime);
        setApprovalLineLifecycle(ApprovalLineLifecycle.PROCESS_UNMODIFIABLE);
    }

    public String getConfirmDocumentId() {
        return this.confirmDocumentId;
    }


    public DocumentType getDocumentType() {
        return this.document.getDocumentType();
    }

    public String getRequesterId() {
        return this.requester.getRequesterId();
    }

    public String getDepartmentId() {
        return this.requester.getDepartmentId();
    }

    public String getCompanyId() {
        return this.requester.getCompanyId();
    }

    public boolean isNotDocumentOwner(Requester requester) {
        return !this.requester.equals(requester);
    }

    public boolean raiseImpossible() {
        return !raisePossible.contains(confirmStatus);
    }

    public boolean cancelImpossible() {
        return !cancelPossible.contains(confirmStatus);
    }

    public boolean confirmStatusNotBelongIn(List<ConfirmStatus> confirmStatuses) {
        return !confirmStatuses.contains(this.confirmStatus);
    }

    public boolean isNotRaiseBefore() {
        return !raiseBefore.contains(confirmStatus);
    }

    // NULL 체크 추가
    public boolean anyApprovalNotAccepted() {
        return approvalLines.stream()
                .anyMatch(approvalLine -> (approvalLine.isNotApproveStatus(ACCEPT) || Objects.isNull(approvalLine.getApproveStatus())));
    }

    public String documentTypeString() {
        return this.getDocumentType().name();
    }

    public void changeApprovalLineCycle(ApprovalLineLifecycle approvalLineLifecycle) {
        this.approvalLineLifecycle = approvalLineLifecycle;
    }

    public boolean approvalLineCreated() {
        return ApprovalLineLifecycle.CREATED.equals(approvalLineLifecycle);
    }

    public Map<String, Object> receiveContents() {
        return this.content.getContents();
    }

    public Long receiveContentPk() {
        return this.content.getPk();
    }
}
