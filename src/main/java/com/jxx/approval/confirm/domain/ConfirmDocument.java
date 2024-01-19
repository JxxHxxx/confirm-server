package com.jxx.approval.confirm.domain;

import com.jxx.approval.confirm.dto.request.Document;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.jxx.approval.confirm.domain.ConfirmStatus.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_CONFIRM_DOCUMENT_MASTER",
        indexes = @Index(name = "IDX_CONFIRM_DOCUMENT_ID", columnList = "CONFIRM_DOCUMENT_ID", unique = true))
public class ConfirmDocument {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIRM_DOCUMENT_PK")
    @Comment(value = "결재 문서 테이블 PK")
    private Long pk;
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

    @OneToMany(mappedBy = "confirmDocument")
    private List<ApprovalLine> approvalLines = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONFIRM_DOCUMENT_CONTENT_PK", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment(value = "결재 문서 본문")
    private ConfirmDocumentContent content;

    @Builder
    public ConfirmDocument(Document document, Requester requester, ConfirmStatus confirmStatus, String createSystem) {
        this.document = document;
        this.requester = requester;
        this.confirmStatus = confirmStatus;
        this.createSystem = createSystem;
        this.createTime = LocalDateTime.now();
    }

    public void setContent(ConfirmDocumentContent content) {
        this.content = content;
    }

    public void changeConfirmStatus(ConfirmStatus confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public String getConfirmDocumentId() {
        return this.document.getConfirmDocumentId();
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
}
