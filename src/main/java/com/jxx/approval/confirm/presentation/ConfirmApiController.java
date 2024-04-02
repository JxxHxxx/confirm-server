package com.jxx.approval.confirm.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.approval.confirm.application.ApprovalLineService;
import com.jxx.approval.confirm.domain.ApproveStatus;
import com.jxx.approval.confirm.domain.ConfirmStatus;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentContentRequest;
import com.jxx.approval.confirm.application.ConfirmDocumentService;
import com.jxx.approval.confirm.dto.request.*;
import com.jxx.approval.confirm.dto.response.*;
import com.jxx.approval.confirm.listener.ApproveStatusChangedEvent;
import com.jxx.approval.confirm.listener.ConfirmStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConfirmApiController {

    private final ConfirmDocumentService confirmDocumentService;
    private final ApprovalLineService approvalLineService;
    private final ApplicationEventPublisher eventPublisher;

    //결재 문서 생성
    @PostMapping("/api/confirm-documents")
    public ResponseEntity<?> save(@RequestBody ConfirmCreateForm form) {
        confirmDocumentService.createConfirmDocument(form);
        return ResponseEntity.ok("생성");
    }

    // 결재 양식 내 본문 생성
    @PostMapping("/api/confirm-documents/{confirm-document-pk}/contents")
    public ResponseEntity<ResponseResult> createContent(@PathVariable(value = "confirm-document-pk")Long confirmDocumentPk ,@RequestBody List<ConfirmDocumentContentRequest> requests) {
        ConfirmDocumentAndContentServiceResponse response = confirmDocumentService.makeContent(confirmDocumentPk, requests);
        return ResponseEntity.ok(new ResponseResult(200, "내용 주입 완료", response));
    }

    // 결재 문서 수정
    @PatchMapping("/api/confirm-documents/{confirm-document-pk}")
    public ResponseEntity<ResponseResult> updateConfirmDocument(@PathVariable(value = "confirm-document-pk") Long confirmDocumentPk,
                                                                @RequestBody ConfirmDocumentUpdateForm form) {
        ConfirmDocumentServiceResponse response = confirmDocumentService.updateConfirmDocument(confirmDocumentPk, form);
        return null;
    }


    // 결재 문서 검색
    @GetMapping("/api/confirm-documents")
    public ResponseEntity<ResponseResult> searchConfirmDocument(@ModelAttribute ConfirmDocumentSearchCondition condition) {
        List<ConfirmDocumentServiceResponse> responses = confirmDocumentService.search(condition);
        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "검색 조회", responses));
    }

    // 결정권자로 포함되어 있는 결재 문서 보기
    @GetMapping("/api/confirm-documents/approval")
    public ResponseEntity<?> findConfirmDocumentForApproval(@ModelAttribute ConfirmDocumentForApprovalSearchCondition condition) {

        List<ConfirmDocumentFetchApprovalLineResponse> responses = confirmDocumentService.findConfirmDocumentForApproval(condition);
        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "결재자로 등록되어 있는 결재 문서 조회", responses));
    }

    // 결재 문서 PK 조회
    @GetMapping("/api/confirm-documents/{confirm-document-pk}")
    public ResponseEntity<ResponseResult<?>> readByPk(@PathVariable(value = "confirm-document-pk") Long confirmDocumentPk) {
        ConfirmDocumentServiceResponse response = confirmDocumentService.readByPk(confirmDocumentPk);

        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "결재 단건 조회", response));
    }

    // 결재 문서 상신
    @PostMapping("/api/confirm-documents/{confirm-document-id}/raise")
    public ResponseEntity<ResponseResult> raise(@PathVariable(name = "confirm-document-id") String confirmDocumentId,
                                   @RequestBody ConfirmRaiseForm form) {
        ConfirmDocumentServiceDto response = confirmDocumentService.raise(confirmDocumentId, form);

        return ResponseEntity.ok(new ResponseResult(OK.value(), "결재 문서 상신", response));
    }

    // 결재선 등록
    @PostMapping("/api/confirm-documents/{confirm-document-id}/approval-lines")
    public ResponseEntity<ResponseResult<ApprovalLineResponse>> enrollApprovalLine(@PathVariable(name = "confirm-document-id") String confirmDocumentId,
                                                @RequestBody List<ApproverEnrollForm> forms) throws JsonProcessingException {
        ApprovalLineResponse approvalLineResponse = approvalLineService.enrollApprovalLines(forms, confirmDocumentId);
        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "결재자 등록", approvalLineResponse));
    }

    // 결재 문서 승인
    @Transactional
    @PatchMapping("/api/confirm-documents/{confirm-document-pk}/accept")
    public ResponseEntity<ResponseResult> acceptConfirmDocument(@PathVariable(name = "confirm-document-pk") Long confirmDocumentPk,
                                                   @RequestBody ApprovalInformationForm form) {

        // ConfirmDocument 에서 결재자가 결재 문서를 승인할 수 있는 상태인지 검증
        eventPublisher.publishEvent(ApproveStatusChangedEvent.acceptEvent(confirmDocumentPk, form.approvalLineId()));
        // 결재 문서 승인 로직
        ApprovalLineServiceResponse response = approvalLineService.accept(confirmDocumentPk, form);
        // 임시
        if (response.finalApproval()) {
            eventPublisher.publishEvent(new ConfirmStatusEvent(confirmDocumentPk, response.vacationId(),ConfirmStatus.ACCEPT));
        }

        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "결재 문서 승인", response));
    }
    // 결재 문서 반려
    @Transactional // 안되겠다. 이벤트 퍼블리셔 서비스레이어에 둬야 할듯.
    @PatchMapping("/api/confirm-documents/{confirm-document-pk}/reject")
    public ResponseEntity<ResponseResult> rejectConfirmDocument(@PathVariable(name = "confirm-document-pk") Long confirmDocumentPk,
                                                   @RequestBody ApprovalInformationForm form) {

        // ConfirmDocument 에서 결재자가 결재 문서를 반려할 수 있는 상태인지 검증
        eventPublisher.publishEvent(ApproveStatusChangedEvent.rejectEvent(confirmDocumentPk, form.approvalLineId()));
        // 결재 문서 반려 로직
        ApprovalLineServiceResponse response = approvalLineService.reject(confirmDocumentPk, form);

        eventPublisher.publishEvent(new ConfirmStatusEvent(confirmDocumentPk, response.vacationId(), ConfirmStatus.REJECT));

        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "결재 문서 반려", response));
    }

    // 결재 문서 취소 - policy 상신 완료된 문서는 취소 불가능
    @PatchMapping("/api/confirm-documents/{confirm-document-pk}/cancel")
    public ResponseEntity<ResponseResult> cancelConfirmDocument(@PathVariable(name = "confirm-document-pk") Long confirmDocumentPk,
                                                   @RequestBody ConfirmDocumentCancelForm form) {
        ConfirmDocumentServiceResponse response = confirmDocumentService.cancelConfirmDocument(confirmDocumentPk, form);

        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "결재 문서 반려", response));
    }
}
