package com.jxx.approval.domain;

import com.jxx.approval.dto.request.Document;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
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

    @Column(name = "CREATE_TIME", nullable = false)
    @Comment(value = "결재 문서 생성 시간")
    private LocalDateTime createTime;

    @OneToMany(mappedBy = "confirmDocument")
    private List<ApprovalLine> approvalLines = new ArrayList<>();

    @Builder
    public ConfirmDocument(Document document, Requester requester, ConfirmStatus confirmStatus, String createSystem) {
        this.document = document;
        this.requester = requester;
        this.confirmStatus = confirmStatus;
        this.createSystem = createSystem;
        this.createTime = LocalDateTime.now();
    }

    public void changeConfirmStatus(ConfirmStatus confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public void checkDocumentRequester(String requesterId) {
        if (!this.requester.isRequester(requesterId)) {
            throw new IllegalArgumentException("결재 문서 내 결재 요청자와 결재를 요청한 사용자가 일치하지 않습니다.");
        };
    }

    public String requesterId() {
        return this.requester.getRequesterId();
    }

    public void verifyWhetherRiseIsPossible() {
        if (!(ConfirmStatus.CREATE.equals(confirmStatus) || ConfirmStatus.UPDATE.equals(confirmStatus) || ConfirmStatus.REJECT.equals(confirmStatus))) {
            throw new IllegalArgumentException("결재 불가합니다. 사유 :" + confirmStatus.getDescription());
        }
    }
}
