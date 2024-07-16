package com.jxx.approval.confirm.domain.form;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_CONFIRM_DOCUMENT_ELEMENT")
public class ConfirmDocumentElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIRM_DOCUMENT_ELEMENT_PK")
    @Comment(value = "결재 문서 요소 테이블 PK")
    private Long pk;
    @Column(name = "CONFIRM_DOCUMENT_ELEMENT_KEY", nullable = false)
    @Comment(value = "결재 문서 요소 KEY")
    private String elementKey;
    @Column(name = "CONFIRM_DOCUMENT_ELEMENT_NAME", nullable = false)
    @Comment(value = "결재 문서 요소 이름")
    private String elementName;
    @Column(name = "CONFIRM_DOCUMENT_ELEMENT_ORDER", nullable = false)
    @Comment(value = "결재 문서 요소 이름")
    private int elementOrder;
    @Column(name = "CONFIRM_DOCUMENT_ELEMENT_GROUP_KEY",nullable = false)
    @Comment(value = "결재 문서 요소 그룹 KEY")
    private String elementGroupKey;
    @Column(name = "CONFIRM_DOCUMENT_ELEMENT_GROUP_NAME", nullable = false)
    @Comment(value = "결재 문서 요소 그룹 명")
    private String elementGroupName;
    @Column(name = "CONFIRM_DOCUMENT_ELEMENT_GROUP_TYPE", nullable = false)
    @Comment(value = "결재 문서 요소 그룹 타입")
    private String elementGroupType;
    @Column(name = "CONFIRM_DOCUMENT_ELEMENT_GROUP_ORDER", nullable = false)
    @Comment(value = "결재 문서 요소 그룹 순서")
    private int elementGroupOrder;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(
                    name = "CONFIRM_DOCUMENT_FORM_ID", referencedColumnName = "CONFIRM_DOCUMENT_FORM_ID",
                    foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)),
            @JoinColumn(name = "CONFIRM_DOCUMENT_FORM_COMPANY_ID", referencedColumnName = "CONFIRM_DOCUMENT_FORM_COMPANY_ID",
                    foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))})
    @Comment(value = "결재 문서 양식 FK(물리적 외래키 X)")
    private ConfirmDocumentForm confirmDocumentForm;

    public ConfirmDocumentElement(String elementKey, String elementName) {
        this.elementKey = elementKey;
        this.elementName = elementName;
    }

    @Builder
    public ConfirmDocumentElement(String elementKey, String elementName, int elementOrder, String elementGroupKey,
                                  String elementGroupName, String elementGroupType, int elementGroupOrder, ConfirmDocumentForm confirmDocumentForm) {
        this.elementKey = elementKey;
        this.elementName = elementName;
        this.elementOrder = elementOrder;
        this.elementGroupKey = elementGroupKey;
        this.elementGroupName = elementGroupName;
        this.elementGroupType = elementGroupType;
        this.elementGroupOrder = elementGroupOrder;
        this.confirmDocumentForm = confirmDocumentForm;
    }

    public void mappingDocumentForm(ConfirmDocumentForm confirmDocumentForm) {
        this.confirmDocumentForm = confirmDocumentForm;
    }
}
