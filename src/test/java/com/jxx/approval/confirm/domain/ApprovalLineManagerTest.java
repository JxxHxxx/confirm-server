package com.jxx.approval.confirm.domain;

import com.jxx.approval.confirm.domain.document.ConfirmDocument;
import com.jxx.approval.confirm.domain.document.ConfirmStatus;
import com.jxx.approval.confirm.domain.document.DocumentType;
import com.jxx.approval.confirm.domain.document.Requester;
import com.jxx.approval.confirm.domain.line.ApprovalLine;
import com.jxx.approval.confirm.domain.line.ApprovalLineException;
import com.jxx.approval.confirm.domain.line.ApprovalLineManager;
import com.jxx.approval.confirm.domain.line.IllegalOrderMethodInvokeException;
import com.jxx.approval.confirm.dto.request.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.jxx.approval.confirm.domain.line.ApprovalLineException.EMPTY_APPROVAL_LINE;
import static com.jxx.approval.confirm.domain.line.ApproveStatus.*;
import static org.assertj.core.api.Assertions.*;

class ApprovalLineManagerTest {

    ApprovalLineManager approvalLineManager;
    ApprovalLineManager emptyApprovalLineManager;
    ApprovalLine approvalLine1;
    ApprovalLine approvalLine2;

    @BeforeEach
    void initialize() {
        ConfirmDocument confirmDocument = ConfirmDocument.builder()
                .requester(new Requester("JXX", "D00001", "U00001"))
                .document(new Document(DocumentType.VAC))
                .confirmStatus(ConfirmStatus.CREATE)
                .createSystem("API")
                .build();

        approvalLine1 = new ApprovalLine(1, "M00001", "이재헌","D00001", "IT사업부", confirmDocument);
        approvalLine2 = new ApprovalLine(2, "M00002", "이유니", "D00001", "IT사업부", confirmDocument);

        emptyApprovalLineManager = ApprovalLineManager.builder()
                .approvalLineId("M00001")
                .approvalLines(List.of())
                .build();
    }

    @DisplayName("결재 라인이 비어있다면 " +
            "ApprovalLineException 예외가 발생하고" +
            "isEmptyApprovalLineFlag 필드는 false 값을 가진다.")
    @Test
    void is_empty_approval_line_fail() {
        assertThatThrownBy(() -> emptyApprovalLineManager.isEmptyApprovalLine())
                        .isInstanceOf(ApprovalLineException.class)
                        .hasMessage(EMPTY_APPROVAL_LINE);

        assertThat(emptyApprovalLineManager.isEmptyApprovalLineFlag()).isFalse();
    }

    @DisplayName("결재 라인이 비어있지 않다면 " +
            "isEmptyApprovalLineFlag 필드는 true 값을 가진다.")
    @Test
    void is_empty_approval_line_success() {
        approvalLineManager = ApprovalLineManager.builder()
                .approvalLineId("M00001")
                .approvalLines(List.of(approvalLine1, approvalLine2))
                .build();

        assertThatCode(() -> approvalLineManager.isEmptyApprovalLine())
                .doesNotThrowAnyException();

        assertThat(approvalLineManager.isEmptyApprovalLineFlag()).isTrue();
    }
    @DisplayName("checkBelongInApprovalLine 메서드를 수행하기 위해서는 반드시 isEmptyApprovalLine 메서드 실행이 선행 되어야 한다. " +
            "만약 isEmptyApprovalLine 메서드를 호출하지 않고 checkBelongInApprovalLine 메서드를 호출한다면" +
            "IllegalOrderMethodInvokeException 예외가 발생한다.")
    @Test
    void check_belong_in_approval_line_fail_cuz_not_call() {
        approvalLineManager = ApprovalLineManager.builder()
                .approvalLineId("M00001")
                .approvalLines(List.of(approvalLine1, approvalLine2))
                .build();

        assertThatThrownBy(() -> approvalLineManager.checkBelongInApprovalLine())
                .isInstanceOf(IllegalOrderMethodInvokeException.class);
    }

    @DisplayName("결재 라인에 속해있지 않은 사용자가 결재(승인/반려)를 요청하려고 하면 " +
            "ApprovalLineException 예외가 발생하며" +
            "isCheckBelongInApprovalLineFlag 는 false 값을 가진다.")
    @Test
    void check_belong_in_approval_line_fail_cuz_not_approval_line() {
        String notApprovalLineId = "M00005";
        approvalLineManager = ApprovalLineManager.builder()
                .approvalLineId(notApprovalLineId)
                .approvalLines(List.of(approvalLine1, approvalLine2))
                .build();

        approvalLineManager.isEmptyApprovalLine();
        assertThatThrownBy(() -> approvalLineManager.checkBelongInApprovalLine())
                .isInstanceOf(ApprovalLineException.class);

        assertThat(approvalLineManager.isCheckBelongInApprovalLineFlag()).isFalse();
    }

    @DisplayName("결재 라인에 속한 사용자가 결재 요청을 시도할 경우, 아래 메서드에서" +
            "예외가 발생하지 않고 " +
            "isCheckBelongInApprovalLineFlag 값은 true 를 가진다.")
    @Test
    void check_belong_in_approval_line_success() {
        String notApprovalLineId = "M00001";
        approvalLineManager = ApprovalLineManager.builder()
                .approvalLineId(notApprovalLineId)
                .approvalLines(List.of(approvalLine1, approvalLine2))
                .build();

        approvalLineManager.isEmptyApprovalLine(); // 사전 작업

        //when - then
        assertThatCode(() -> approvalLineManager.checkBelongInApprovalLine())
                .doesNotThrowAnyException();

        assertThat(approvalLineManager.isCheckBelongInApprovalLineFlag()).isTrue();
    }

    @DisplayName("자신의 결재 순서가 아닐 때 결재를 요청하는 경우" +
            "ApprovalLineException 예외 발생 " +
            "checkApprovalOrderLineFlag 은 false 값을 가진다.")
    @Test
    void check_approval_line_order_fail_cuz_not_requester_turn() {
        String notApprovalLineId = "M00002";
        approvalLineManager = ApprovalLineManager.builder()
                .approvalLineId(notApprovalLineId)
                .approvalLines(List.of(approvalLine1, approvalLine2))
                .build();

        approvalLineManager.isEmptyApprovalLine(); // 사전 작업
        approvalLineManager.checkBelongInApprovalLine(); // 사전 작업

        assertThatThrownBy(() -> approvalLineManager.checkApprovalLineOrder())
                .isInstanceOf(ApprovalLineException.class)
                .hasMessageContaining("당신의 차례가 아닙니다.");

        assertThat(approvalLineManager.isCheckApprovalOrderLineFlag()).isFalse();
    }

    @DisplayName("이전 결재자가 반려한 문서는 결재를 요청할 수 없다." +
            "만약 요청할 경우 ApprovalLineException 예외 발생하고 " +
            "isCheckApprovalOrderLineFlag 는 false 값을 가진다.")
    @Test
    void check_approval_line_order_fail_cuz_already_reject() {
        String notApprovalLineId = "M00002";
        approvalLine1.changeApproveStatus(REJECT); // 이전 결재자의 문서 반려 처리

        approvalLineManager = ApprovalLineManager.builder()
                .approvalLineId(notApprovalLineId)
                .approvalLines(List.of(approvalLine1, approvalLine2))
                .build();

        approvalLineManager.isEmptyApprovalLine(); // 사전 작업
        approvalLineManager.checkBelongInApprovalLine(); // 사전 작업

        //when - then
        assertThatThrownBy(() -> approvalLineManager.checkApprovalLineOrder())
                .isInstanceOf(ApprovalLineException.class)
                .hasMessageContaining("반려된 결재 문서입니다.");

        assertThat(approvalLineManager.isCheckApprovalOrderLineFlag()).isFalse();
    }

    @DisplayName("자신의 순서일 때 호출하는 경우, " +
            "어떠한 예외도 발생하지 않고" +
            "메서드 호출 플래그 isCheckApprovalOrderLineFlag 는 true 값을 가진다.")
    @Test
    void check_approval_line_order_success() {
        String notApprovalLineId = "M00001";
        approvalLineManager = ApprovalLineManager.builder()
                .approvalLineId(notApprovalLineId)
                .approvalLines(List.of(approvalLine1, approvalLine2))
                .build();

        approvalLineManager.isEmptyApprovalLine(); // 사전 작업
        approvalLineManager.checkBelongInApprovalLine(); // 사전 작업

        assertThatCode(() -> approvalLineManager.checkApprovalLineOrder())
                .doesNotThrowAnyException();

        assertThat(approvalLineManager.isCheckApprovalOrderLineFlag()).isTrue();
    }

    @DisplayName("결재 요청을 위한 사전 메서드" +
            "1. isEmptyApprovalLine" +
            "2. checkBelongInApprovalLine" +
            "3. checkApprovalLineOrder " +
            "를 모두 호출한 뒤 changeApproveStatus 메서드를 호출할 경우 " +
            "어떠한 예외도 변경되지 않으며 " +
            "해당 결재자의 결재 상태 값이 변경된다.")
    @Test
    void changeApproveStatus() {
        String notApprovalLineId = "M00001";
        approvalLineManager = ApprovalLineManager.builder()
                .approvalLineId(notApprovalLineId)
                .approvalLines(List.of(approvalLine1, approvalLine2))
                .build();

        approvalLineManager.isEmptyApprovalLine(); // 사전 작업
        approvalLineManager.checkBelongInApprovalLine(); // 사전 작업
        approvalLineManager.checkApprovalLineOrder(); // 사전 작업

        assertThatCode(() -> approvalLineManager.changeApproveStatus(ACCEPT))
                .doesNotThrowAnyException();

        assertThat(approvalLineManager.getApprovalLine().getApproveStatus().equals(ACCEPT)).isTrue();
        assertThat(approvalLineManager.getApprovalLines().get(1).getApproveStatus().equals(PENDING)).isTrue();
    }
}