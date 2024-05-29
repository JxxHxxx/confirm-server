package com.jxx.approval.confirm.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.approval.confirm.domain.document.*;
import com.jxx.approval.confirm.domain.line.ApprovalLineLifecycle;
import com.jxx.approval.confirm.dto.request.ApprovalLineEnrollForm;
import com.jxx.approval.confirm.dto.request.Document;
import com.jxx.approval.confirm.dto.response.ApprovalLineResponse;
import com.jxx.approval.confirm.infra.ConfirmDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jxx.approval.confirm.domain.document.ConfirmStatus.CREATE;
import static org.assertj.core.api.Assertions.*;

@Slf4j
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

    @DisplayName("결재 문서가 생성된 시점에는 결재선을 지정할 수 있다." +
            "main 애플리케이션 코드에서는 결재선에 지정된 사용자가 " +
            "사내 구성원인지 검증하는 consumer 함수형 인터페이스 구현체를 주입한다.")
    @Test
    void enroll_approvals_success_case() throws JsonProcessingException {
        ApprovalLineEnrollForm enrollForm1 = new ApprovalLineEnrollForm("U00001", "이재헌","D00001", "IT사업부",1);
        ApprovalLineEnrollForm enrollForm2 = new ApprovalLineEnrollForm("U00002", "이유니","D00001", "IT사업부",2);

        ApprovalLineResponse approvalLineResponse = approvalLineService.enrollApprovalLines(
                List.of(enrollForm1, enrollForm2), confirmDocumentId, o -> log.info("call verify are they company members?"));

        assertThat(approvalLineResponse.approvalLines())
                .extracting("approvalId").containsExactly("U00001", "U00002");
    }

    @DisplayName("이미 결재선이 등록된 결재 문서에 결재선을 추가로 지정할 수 없다.")
    @Test
    void enroll_approvals_fail_case_already_enrolled() throws JsonProcessingException {
        //given
        ApprovalLineEnrollForm enrollForm1 = new ApprovalLineEnrollForm("U00001", "이재헌","D00001", "IT사업부",1);
        ApprovalLineEnrollForm enrollForm2 = new ApprovalLineEnrollForm("U00002", "이유니","D00001", "IT사업부",2);

        // 한번 결재선을 등록
        approvalLineService.enrollApprovalLines(
                List.of(enrollForm1, enrollForm2), confirmDocumentId, o -> log.info("call verify are they company members?"));
        //when - then
        assertThatThrownBy(() -> approvalLineService.enrollApprovalLines
                (List.of(enrollForm1, enrollForm2), confirmDocumentId, o -> log.info("call verify are they company members?")))
                .isInstanceOf(ConfirmDocumentException.class)
                .hasMessageContaining("이미 결재선이 지정된 결재 문서입니다.");
    }

    @DisplayName("결재선은 상신 전 상태(ConfirmStatus in create, update)에서만 지울 수 있다." +
            "그 외의 상태일 경우에는 ConfirmDocumentException 이 발생한다.")
    @ParameterizedTest
    @EnumSource(value = ConfirmStatus.class, names = {"ACCEPT", "REJECT", "RAISE", "CANCEL"})
    void name(ConfirmStatus confirmStatus) {
        //given
        ConfirmDocument confirmDocument = confirmDocumentRepository.findWithContent(confirmDocumentId).get();
        confirmDocument.changeConfirmStatus(confirmStatus);
        confirmDocumentRepository.flush();
        //when - then
        assertThatThrownBy(() -> approvalLineService.deleteApprovalLines(confirmDocumentId, confirmDocument.getRequesterId()))
                .isInstanceOf(ConfirmDocumentException.class);
    }
}