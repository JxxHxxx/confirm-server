package com.jxx.approval.confirm.domain;

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
@Table(name = "JXX_CONFIRM_DOCUMENT_FORM_ELEMENT")
public class ConfirmDocumentFormElement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIRM_DOCUMENT_FORM_ELEMENT_PK")
    @Comment(value = "결재 문서 양식/요소 매핑 테이블 PK")
    private Long pk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONFIRM_DOCUMENT_ELEMENT_PK", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ConfirmDocumentElement confirmDocumentElement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONFIRM_DOCUMENT_FORM_PK", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ConfirmDocumentForm confirmDocumentForm;

    private LocalDateTime createTime;

    @Builder
    public ConfirmDocumentFormElement(ConfirmDocumentElement confirmDocumentElement, ConfirmDocumentForm confirmDocumentForm) {
        this.confirmDocumentElement = confirmDocumentElement;
        this.confirmDocumentForm = confirmDocumentForm;
        this.createTime = LocalDateTime.now();
    }
}
