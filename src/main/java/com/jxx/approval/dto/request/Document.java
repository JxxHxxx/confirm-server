package com.jxx.approval.dto.request;

import com.jxx.approval.domain.DocumentType;
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

    @Column(name = "CONFIRM_DOCUMENT_ID", nullable = false, unique = true)
    @Comment(value = "결재 문서 ID")
    private String confirmDocumentId;

    @Column(name = "DOCUMENT_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment(value = "결재 양식 종류(ex 휴가, 구매 신청)")
    private DocumentType documentType;

    public Document(String confirmDocumentId, DocumentType documentType) {
        this.confirmDocumentId = confirmDocumentId;
        this.documentType = documentType;
    }
}
