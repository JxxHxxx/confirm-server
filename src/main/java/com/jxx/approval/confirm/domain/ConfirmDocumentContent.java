package com.jxx.approval.confirm.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;

import java.util.HashMap;
import java.util.Map;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_CONFIRM_DOCUMENT_CONTENT_MASTER")
public class ConfirmDocumentContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIRM_DOCUMENT_CONTENT_PK")
    @Comment(value = "결재 문서 테이블 PK")
    private Long pk;

    @Type(JsonType.class)
    @Column(name = "CONTENTS", columnDefinition = "json")
    private Map<String, Object> contents = new HashMap<>();

    public ConfirmDocumentContent(Map<String, Object> content) {
        this.contents = content;
    }
}
