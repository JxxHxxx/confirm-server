package com.jxx.approval.domain;

import com.jxx.approval.dto.request.Document;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "confirmDocument")
    private List<Approver> approvers = new ArrayList<>();

    @Builder
    public ConfirmDocument(Document document, Requester requester, ConfirmStatus confirmStatus, String createSystem) {
        this.document = document;
        this.requester = requester;
        this.confirmStatus = confirmStatus;
        this.createSystem = createSystem;
    }

    public void changeConfirmStatus(ConfirmStatus confirmStatus) {
        this.confirmStatus = confirmStatus;
    }
}
