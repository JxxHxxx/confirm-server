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
                @Index(name = "IDX_CREATE_TIME", columnList = "CREATE_TIME", unique = false),
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfirmDocument that = (ConfirmDocument) o;
        return Objects.equals(pk, that.pk) && Objects.equals(confirmDocumentId, that.confirmDocumentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pk, confirmDocumentId);
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
     * <pre>
     * WRITE QUERY : JPA dirty checking
     * 최종 결정된 결재 문서 후속 처리
     * 최종 결정된 문서란
     * 1) 최종 결정권자의 승인
     * 2) 결재자 중 1인 반려가 된 문서를 말한다.
     * </pre>
     **/
    public void processFinalDecisionConfirmDocument(ConfirmStatus confirmStatus) {
        changeConfirmStatus(confirmStatus);
        setCompletedTime(LocalDateTime.now());
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

    // 결재 라인 중, ACCEPT 가 아닌 플래그가 있는지 검사
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

    // 결재선이 생성됐는지 확인 여부, BEFORE_CREATE 상태이면 결재선이 생성되지 않은 상태
    public boolean approvalLineNotCreated() {
        return ApprovalLineLifecycle.BEFORE_CREATE.equals(approvalLineLifecycle);
    }

    public Map<String, Object> receiveContents() {
        return this.content.getContents();
    }

    public Long receiveContentPk() {
        return this.content.getPk();
    }

    /**
     * 사내 결재 문서인지 검증
     *
     * @Param : memberCompanyId 사용자의 회사 코드
     * @Return - true : 결재문서 회사코드와 사용자 회사코드 일치
     * - false : 결재문서 회사코드와 사용자 회사코드 불일치
     **/
    public boolean areOurCompanyConfirmDocument(String memberCompanyId) {
        return Objects.equals(requester.getCompanyId(), memberCompanyId);
    }

    /**
     * @param confirmStatus : 해당 상태 값으로 변경 가능한지 검증
     **/
    public boolean isNotChangeTo(ConfirmStatus confirmStatus) {
        switch (confirmStatus) {
            case ACCEPT -> {
                return !ACCEPT_POSSIBLE_STATUS.contains(this.confirmStatus);
            }
            case REJECT -> {
                return !REJECT_POSSIBLE_STATUS.contains(this.confirmStatus);
            }
            default -> {
                return false;
            }
        }
    }
}
