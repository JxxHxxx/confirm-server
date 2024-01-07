package com.jxx.approval.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_CONFIRM_DOCUMENT_MASTER", indexes = @Index(name = "IDX_CONFIRM_DOCUMENT_ID", columnList = "CONFIRM_DOCUMENT_ID", unique = true))
public class ConfirmDocument {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIRM_DOCUMENT_PK")
    @Comment(value = "결제 테이블 PK")
    private Long pk;

    @Column(name = "CONFIRM_DOCUMENT_ID", nullable = false, unique = true)
    @Comment(value = "결재 문서 ID")
    private String confirmDocumentId;

    @Column(name = "COMPANY_ID", nullable = false)
    @Comment(value = "회사 ID")
    private String companyId;

    @Column(name = "DEPARTMENT_ID", nullable = false)
    @Comment(value = "부서 ID")
    private String departmentId;

    @Column(name = "CREATE_SYSTEM", nullable = false)
    @Comment(value = "결재 데이터를 생성한 시스템")
    private String createSystem;

    @Column(name = "CONFIRM_STATUS", nullable = false)
    @Comment(value = "결재 상태")
    @Enumerated(EnumType.STRING)
    private ConfirmStatus confirmStatus;

    @Column(name = "DOCUMENT_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment(value = "결재 양식 종류(ex 휴가, 구매 신청)")
    private DocumentType documentType;

    @Column(name = "APPROVAL_ID", nullable = true)
    @Comment(value = "결재자 ID")
    private String approvalId;

    @Builder
    public ConfirmDocument(String confirmDocumentId, String companyId, String departmentId, String createSystem, ConfirmStatus confirmStatus, DocumentType documentType, String approvalId) {
        this.confirmDocumentId = confirmDocumentId;
        this.companyId = companyId;
        this.departmentId = departmentId;
        this.createSystem = createSystem;
        this.confirmStatus = confirmStatus;
        this.documentType = documentType;
        this.approvalId = approvalId;
    }

    public void changeConfirmStatus(ConfirmStatus confirmStatus) {
        this.confirmStatus = confirmStatus;
    }
}
