package com.jxx.approval.confirm.domain.form;

import jakarta.persistence.*;
import lombok.AccessLevel;
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
    // ID, KEY 만 있으면 될듯
    @Column(name = "CONFIRM_DOCUMENT_ELEMENT_KEY", nullable = false)
    @Comment(value = "결재 문서 요소 KEY")
    private String elementKey;
    @Column(name = "CONFIRM_DOCUMENT_ELEMENT_NAME", nullable = false)
    @Comment(value = "결재 문서 요소 KEY")
    private String ElementName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONFIRM_DOCUMENT_FORM_ID", referencedColumnName = "CONFIRM_DOCUMENT_FORM_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment(value = "결재 문서 양식")
    private ConfirmDocumentForm confirmDocumentForm;

    public ConfirmDocumentElement(String elementKey, String elementName) {
        this.elementKey = elementKey;
        this.ElementName = elementName;
    }

    public void mappingDocumentForm(ConfirmDocumentForm confirmDocumentForm) {
        this.confirmDocumentForm = confirmDocumentForm;
    }
}
