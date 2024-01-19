package com.jxx.approval.confirm.domain;

import com.jxx.approval.confirm.domain.ConfirmDocument;
import com.jxx.approval.confirm.domain.ConfirmStatus;
import com.jxx.approval.confirm.domain.DocumentType;
import com.jxx.approval.confirm.domain.Requester;
import com.jxx.approval.confirm.dto.request.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static com.jxx.approval.confirm.domain.ConfirmStatus.*;
import static org.assertj.core.api.Assertions.*;

class ConfirmDocumentTest {

    @DisplayName("결재 문서 생성자임을 확인한다. " +
            "결재 문서 생성자일 시 false 반환" +
            "결재 문서 생성자가 아닐 시 true 반환")
    @ParameterizedTest(name = "[{index}] companyId : {0} departmentId : {1} requesterId : {2} ")
    @CsvSource(value = {
            "JXX, J00001, U00001, false",
            "PXX, J00001, U00001, true",
            "JXX, J00002, U00001, true",
            "JXX, J00001, U00002, true",}, delimiter = ',')
    void is_not_document_owner(String companyId, String departmentId, String requesterId, boolean isNotDocumentOwner) {
        //given
        ConfirmDocument confirmDocument = ConfirmDocument.builder()
                .requester(new Requester("JXX", "J00001", "U00001"))
                .document(new Document(DocumentType.VAC))
                .createSystem("API")
                .confirmStatus(CREATE)
                .build();
        //when
        Requester requester = new Requester(companyId, departmentId, requesterId);
        //than
        assertThat(confirmDocument.isNotDocumentOwner(requester)).isEqualTo(isNotDocumentOwner);
    }

    @DisplayName(value = "상신 가능한 문서인지를 검증한다." +
            "ConfirmStatus 상태가 RAISE, CANCEL, ACCEPT 중 하나라면 true 를 반환한다.")
    @ParameterizedTest
    @EnumSource(value = ConfirmStatus.class, names = {"RAISE", "CANCEL", "ACCEPT"})
    void raise_impossible_return_true_case(ConfirmStatus confirmStatus) {
        ConfirmDocument confirmDocument = ConfirmDocument.builder()
                .requester(new Requester("JXX", "J00001", "U00001"))
                .document(new Document(DocumentType.VAC))
                .createSystem("API")
                .confirmStatus(confirmStatus)
                .build();

        boolean isRaiseImpossible = confirmDocument.raiseImpossible();

        assertThat(isRaiseImpossible).isTrue();
    }

    @DisplayName(value = "상신 가능한 문서인지를 검증한다." +
            "ConfirmStatus 상태가 CREATE, UPDATE, REJECT 중 하나라면 false 를 반환한다.")
    @ParameterizedTest
    @EnumSource(value = ConfirmStatus.class, names = {"CREATE", "UPDATE", "REJECT"})
    void raise_impossible_return_false_case(ConfirmStatus confirmStatus) {
        ConfirmDocument confirmDocument = ConfirmDocument.builder()
                .requester(new Requester("JXX", "J00001", "U00001"))
                .document(new Document(DocumentType.VAC))
                .createSystem("API")
                .confirmStatus(confirmStatus)
                .build();

        boolean isRaiseImpossible = confirmDocument.raiseImpossible();

        assertThat(isRaiseImpossible).isFalse();
    }
    @DisplayName(value = "파기(리스소 생성자에 의한) 가능한 문서인지를 검증한다." +
            "ConfirmStatus 상태가 RAISE, CANCEL, ACCEPT 중 하나라면 true 를 반환한다.")
    @ParameterizedTest
    @EnumSource(value = ConfirmStatus.class, names = {"RAISE", "CANCEL", "ACCEPT"})
    void cancel_impossible_return_true_case(ConfirmStatus confirmStatus) {
        ConfirmDocument confirmDocument = ConfirmDocument.builder()
                .requester(new Requester("JXX", "J00001", "U00001"))
                .document(new Document(DocumentType.VAC))
                .createSystem("API")
                .confirmStatus(confirmStatus)
                .build();

        boolean isCancelImpossible = confirmDocument.cancelImpossible();

        assertThat(isCancelImpossible).isTrue();
    }

    @DisplayName(value = "파기(리스소 생성자에 의한) 가능한 문서인지를 검증한다." +
            "ConfirmStatus 상태가 CREATE, UPDATE, REJECT 중 하나라면 false 를 반환한다.")
    @ParameterizedTest
    @EnumSource(value = ConfirmStatus.class, names = {"CREATE", "UPDATE", "REJECT"})
    void cancel_impossible_return_false_case(ConfirmStatus confirmStatus) {
        ConfirmDocument confirmDocument = ConfirmDocument.builder()
                .requester(new Requester("JXX", "J00001", "U00001"))
                .document(new Document(DocumentType.VAC))
                .createSystem("API")
                .confirmStatus(confirmStatus)
                .build();

        boolean isCancelImpossible = confirmDocument.cancelImpossible();

        assertThat(isCancelImpossible).isFalse();
    }

    @DisplayName("현재 결재 문서의 상태가 특정 ConfirmStatus 그룹에 속하는지 여부를 판단한다." +
            "속한다면 false" +
            "속하지 않는다면 true 를 반환한다.")
    @Test
    void confirm_status_belong_in() {
        ConfirmDocument confirmDocument = ConfirmDocument.builder()
                .requester(new Requester("JXX", "J00001", "U00001"))
                .document(new Document(DocumentType.VAC))
                .createSystem("API")
                .confirmStatus(CREATE)
                .build();

        List<ConfirmStatus> confirmStatusGroup = List.of(REJECT, UPDATE);
        boolean isNotBelongInGroup = confirmDocument.confirmStatusNotBelongIn(confirmStatusGroup);

        assertThat(isNotBelongInGroup).isTrue();
    }
}