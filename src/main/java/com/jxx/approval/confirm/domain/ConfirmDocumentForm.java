package com.jxx.approval.confirm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_CONFIRM_DOCUMENT_FORM")
public class ConfirmDocumentForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIRM_DOCUMENT_FORM_PK")
    @Comment(value = "결재 문서 요소 테이블 PK")
    private Long pk;

    @Column(name = "CONFIRM_DOCUMENT_FORM_ID", nullable = false, unique = true)
    @Comment(value = "결재 문서 양식 ID")
    private String formId;
    @Column(name = "CONFIRM_DOCUMENT_FORM_NAME")
    @Comment(value = "결재 문서 양식 이름")
    private String formName;

    @Column(name = "CONFIRM_DOCUMENT_FORM_COMPANY_ID", nullable = false)
    @Comment(value = "결재 문서 양식 회사 ID")
    private String companyId;

    public ConfirmDocumentForm(String formId, String formName, String companyId) {
        this.formId = formId;
        this.formName = formName;
        this.companyId = companyId;
    }
}
