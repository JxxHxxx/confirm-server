package com.jxx.approval.application;

import com.jxx.approval.domain.ConfirmDocument;
import com.jxx.approval.domain.DocumentType;
import com.jxx.approval.domain.Requester;
import com.jxx.approval.dto.request.ApproverEnrollForm;
import com.jxx.approval.dto.request.Document;
import com.jxx.approval.infra.ConfirmDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jxx.approval.domain.ConfirmStatus.CREATE;

@Transactional
@SpringBootTest
class ApprovalLineServiceTest {

    @Autowired
    ApprovalLineService approvalLineService;

    @Autowired
    ConfirmDocumentRepository confirmDocumentRepository;

    // 결재 라인 테스트에 사용될 결재 문서 PK - beforeEach 를 통해 필드 값 초기화
    Long confirmDocumentPk;
    @BeforeEach
    void beforeEach() {
        ConfirmDocument confirmDocument = ConfirmDocument.builder()
                .requester(new Requester("JXX", "J00001", "U00001"))
                .document(new Document(DocumentType.VAC))
                .createSystem("API")
                .confirmStatus(CREATE)
                .build();

        ConfirmDocument savedConfirmDocument = confirmDocumentRepository.save(confirmDocument);
        confirmDocumentPk = savedConfirmDocument.getPk();
    }

    @Test
    void enroll_approvals() {
        ApproverEnrollForm enrollForm1 = new ApproverEnrollForm("U00001", 1);
        ApproverEnrollForm enrollForm2 = new ApproverEnrollForm("U00001", 2);
        approvalLineService.enrollApprovals(List.of(enrollForm1, enrollForm2), confirmDocumentPk);
    }
}