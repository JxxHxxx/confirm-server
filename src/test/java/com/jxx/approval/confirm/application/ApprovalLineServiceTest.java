package com.jxx.approval.confirm.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.approval.confirm.domain.ConfirmDocument;
import com.jxx.approval.confirm.domain.DocumentType;
import com.jxx.approval.confirm.domain.Requester;
import com.jxx.approval.confirm.dto.request.ApproverEnrollForm;
import com.jxx.approval.confirm.dto.request.Document;
import com.jxx.approval.confirm.infra.ConfirmDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jxx.approval.confirm.domain.ConfirmStatus.CREATE;

@Transactional
@SpringBootTest
class ApprovalLineServiceTest {

    @Autowired
    ApprovalLineService approvalLineService;
    @Autowired
    ConfirmDocumentRepository confirmDocumentRepository;

    // 결재 라인 테스트에 사용될 결재 문서 ID - beforeEach 를 통해 필드 값 초기화
    String confirmDocumentId;
    @BeforeEach
    void beforeEach() {
        ConfirmDocument confirmDocument = ConfirmDocument.builder()
                .requester(new Requester("JXX", "J00001", "U00001"))
                .document(new Document(DocumentType.VAC))
                .createSystem("API")
                .confirmStatus(CREATE)
                .build();

        ConfirmDocument savedConfirmDocument = confirmDocumentRepository.save(confirmDocument);
        confirmDocumentId = savedConfirmDocument.getConfirmDocumentId();
    }

    @Test
    void enroll_approvals() throws JsonProcessingException {
        ApproverEnrollForm enrollForm1 = new ApproverEnrollForm("U00001", 1);
        ApproverEnrollForm enrollForm2 = new ApproverEnrollForm("U00001", 2);
        approvalLineService.enrollApprovalLines(List.of(enrollForm1, enrollForm2), confirmDocumentId);
    }
}