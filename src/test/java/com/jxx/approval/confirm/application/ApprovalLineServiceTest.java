package com.jxx.approval.confirm.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.approval.confirm.domain.ApprovalLineLifecycle;
import com.jxx.approval.confirm.domain.ConfirmDocument;
import com.jxx.approval.confirm.domain.DocumentType;
import com.jxx.approval.confirm.domain.Requester;
import com.jxx.approval.confirm.dto.request.ApprovalLineEnrollForm;
import com.jxx.approval.confirm.dto.request.Document;
import com.jxx.approval.confirm.infra.ConfirmDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
                .confirmDocumentId("VACJXX1")
                .requester(new Requester("JXX", "J00001", "U00001"))
                .document(new Document(DocumentType.VAC))
                .createSystem("API")
                .approvalLineLifecycle(ApprovalLineLifecycle.BEFORE_CREATE)
                .confirmStatus(CREATE)
                .build();

        ConfirmDocument savedConfirmDocument = confirmDocumentRepository.save(confirmDocument);
        confirmDocumentId = savedConfirmDocument.getConfirmDocumentId();
    }

    @Disabled
    @Test
    void enroll_approvals() throws JsonProcessingException {
        ApprovalLineEnrollForm enrollForm1 = new ApprovalLineEnrollForm("U00001", "이재헌","D00001", "IT사업부",1);
        ApprovalLineEnrollForm enrollForm2 = new ApprovalLineEnrollForm("U00001", "이유니","D00001", "IT사업부",2);

        approvalLineService.enrollApprovalLines(List.of(enrollForm1, enrollForm2), confirmDocumentId);
    }
}