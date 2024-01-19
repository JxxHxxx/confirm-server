package com.jxx.approval.confirm.domain;

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

    @Column(name = "CONFIRM_DOCUMENT_ELEMENT_KEY", nullable = false, unique = true)
    @Comment(value = "결재 문서 요소 KEY")
    private String key;
    @Column(name = "CONFIRM_DOCUMENT_ELEMENT_NAME", nullable = false)
    @Comment(value = "결재 문서 요소 이름")
    private String name;

    public ConfirmDocumentElement(String key, String name) {
        this.key = key;
        this.name = name;
    }
}
