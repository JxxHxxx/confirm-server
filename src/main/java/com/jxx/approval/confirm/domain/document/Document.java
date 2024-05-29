package com.jxx.approval.confirm.domain.document;

import com.jxx.approval.confirm.domain.document.DocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Document {

    @Column(name = "DOCUMENT_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment(value = "결재 양식 종류(ex 휴가, 구매 신청)")
    private DocumentType documentType;

    public Document(DocumentType documentType) {
        this.documentType = documentType;
    }
}
