package com.jxx.approval.confirm.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.approval.confirm.application.ApprovalLineService;
import com.jxx.approval.confirm.domain.document.ConfirmDocumentException;
import com.jxx.approval.confirm.domain.document.ConfirmStatus;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConfirmDocumentApiController {

    private final ConfirmDocumentService confirmDocumentService;
    private final ApprovalLineService approvalLineService;
    private final ApplicationEventPublisher eventPublisher;

    private final static String WHITE_SPACE = "";

    //결재 문서 생성
    @PostMapping("/api/confirm-documents")
    public ResponseEntity<?> save(@RequestBody ConfirmCreateForm form) {
        confirmDocumentService.createConfirmDocument(form);
        return ResponseEntity.ok("생성");
    }

    @GetMapping("/api/confirm-documents/{confirm-document-id}")
    public ResponseEntity<ResponseResult<?>> findByConfirmDocumentId(@PathVariable(value = "confirm-document-id") String confirmDocumentId) {
        if (Objects.equals(confirmDocumentId.trim(), WHITE_SPACE) || Objects.isNull(confirmDocumentId)) {
            throw new ConfirmDocumentException("결재 문서 ID 는 NULL, 공백일 수 없습니다");
        }
        List<ConfirmDocumentServiceResponse> response = confirmDocumentService.findByConfirmDocumentId(confirmDocumentId);

        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "결재 단건 조회", response));
    }

    @GetMapping("/api/confirm-documents/search")
    public ResponseEntity<?> searchDocuments(@ModelAttribute ConfirmDocumentSearchCondition condition) {
        List<ConfirmDocumentServiceResponse> responses = confirmDocumentService.searchDocuments(condition);
        return ResponseEntity.ok((new ResponseResult<>(200, "결재 문서 검색 완료",  responses)));
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

    // 결재 문서 + 결재 라인 + 컨텐츠 검색
    @GetMapping("/api/confirm-documents/fetch-approval-lines")
    public ResponseEntity<?> findWithApprovalLines(@ModelAttribute ConfirmDocumentSearchCondition condition) {
        List<ConfirmDocumentWithApprovalLineResponse> responses = confirmDocumentService.fetchWithApprovalLines(condition);
        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "결재 문서, 결재 라인 조회", responses));
    }

    @GetMapping("/api/confirm-documents/written-self")
    public ResponseEntity<?> findConfirmDocuments(@RequestParam(name = "companyId") String companyId,
                                                  @RequestParam(name = "departmentId") String departmentId,
                                                  @RequestParam(name = "requesterId") String requesterId) {
        List<ConfirmDocumentServiceResponse> responses = confirmDocumentService
                .findSelfDraftConfirmDocuments(companyId, departmentId, requesterId);
        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "내가 기안한 결재 문서 조회", responses));
    }

    @GetMapping("/api/confirm-documents/search-my-department")
    public ResponseEntity<?> findDepartmentConfirmDocument(@RequestParam(name = "companyId") String companyId,
                                                           @RequestParam(name = "departmentId") String departmentId) {
        List<ConfirmDocumentServiceResponse> responses = confirmDocumentService.findDepartmentConfirmDocument(companyId, departmentId);
        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "부서 결재함 조회", responses));
    }

    // 결재선 등록
    @PostMapping("/api/confirm-documents/{confirm-document-id}/approval-lines")
    public ResponseEntity<ResponseResult<ApprovalLineResponse>> enrollApprovalLine(@PathVariable(name = "confirm-document-id") String confirmDocumentId,
                                                                                   @RequestBody List<ApprovalLineEnrollForm> forms) throws JsonProcessingException {
        ApprovalLineResponse approvalLineResponse = approvalLineService.enrollApprovalLines(forms, confirmDocumentId);
        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "결재자 등록", approvalLineResponse));
    }

    // 결재선 삭제
    @DeleteMapping("/api/confirm-documents/{confirm-document-id}/approval-lines")
    public ResponseEntity<?> deleteApprovalLines(@PathVariable(name = "confirm-document-id") String confirmDocumentId, @RequestBody String memberId){
        ApprovalLineResponse response = approvalLineService.deleteApprovalLines(confirmDocumentId, memberId);
        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "결재선 삭제 완료", response));
    }
    @GetMapping("/api/confirm-documents/{confirm-document-id}/approval-lines")
    public ResponseEntity<?> findApprovalLines(@PathVariable("confirm-document-id") String confirmDocumentId,
                                               @RequestParam(value = "companyId") String companyId) {

        List<ApprovalLineServiceDto> responses = approvalLineService.findByConfirmDocumentId(confirmDocumentId, companyId);
        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "결재선 조회", responses));
    }

    // 결재 문서 상신
    @PostMapping("/api/confirm-documents/{confirm-document-id}/raise")
    public ResponseEntity<ResponseResult> raise(@PathVariable(name = "confirm-document-id") String confirmDocumentId,
                                   @RequestBody ConfirmRaiseForm form) {
        ConfirmDocumentServiceDto response = confirmDocumentService.raise(confirmDocumentId, form);

        return ResponseEntity.ok(new ResponseResult(OK.value(), "결재 문서 상신", response));
    }
    // 결재 문서 승인
    @Transactional
    @PatchMapping("/api/confirm-documents/{confirm-document-id}/accept")
    public ResponseEntity<ResponseResult> acceptConfirmDocument(@PathVariable(name = "confirm-document-id") String confirmDocumentId,
                                                   @RequestBody ApprovalInformationForm form) {

        // ConfirmDocument 에서 결재자가 결재 문서를 승인할 수 있는 상태인지 검증
        eventPublisher.publishEvent(ApproveStatusChangedEvent.acceptEvent(confirmDocumentId, form.approvalLineId()));
        // 결재 문서 승인 로직
        ApprovalLineServiceResponse response = approvalLineService.accept(confirmDocumentId, form);
        // 임시
        if (response.finalApproval()) {
            eventPublisher.publishEvent(new ConfirmStatusEvent(confirmDocumentId, response.vacationId(),ConfirmStatus.ACCEPT, LocalDateTime.now()));
        }

        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "결재 문서 승인", response));
    }
    // 결재 문서 반려
    @Transactional // 안되겠다. 이벤트 퍼블리셔 서비스레이어에 둬야 할듯.
    @PatchMapping("/api/confirm-documents/{confirm-document-id}/reject")
    public ResponseEntity<ResponseResult> rejectConfirmDocument(@PathVariable(name = "confirm-document-id") String confirmDocumentId,
                                                   @RequestBody ApprovalInformationForm form) {

        // ConfirmDocument 에서 결재자가 결재 문서를 반려할 수 있는 상태인지 검증
        eventPublisher.publishEvent(ApproveStatusChangedEvent.rejectEvent(confirmDocumentId, form.approvalLineId()));
        // 결재 문서 반려 로직
        ApprovalLineServiceResponse response = approvalLineService.reject(confirmDocumentId, form);

        eventPublisher.publishEvent(new ConfirmStatusEvent(confirmDocumentId, response.vacationId(), ConfirmStatus.REJECT, LocalDateTime.now()));

        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "결재 문서 반려", response));
    }

    // 결재 문서 취소 - policy 상신 완료된 문서는 취소 불가능
    @PatchMapping("/api/confirm-documents/{confirm-document-id}/cancel")
    public ResponseEntity<ResponseResult> cancelConfirmDocument(@PathVariable(name = "confirm-document-id") String confirmDocumentId,
                                                   @RequestBody ConfirmDocumentCancelForm form) {
        ConfirmDocumentServiceDto response = confirmDocumentService.cancelConfirmDocument(confirmDocumentId, form);

        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "결재 문서 반려", response));
    }

    @GetMapping("/api/confirm-documents/contents/{content-pk}")
    public ResponseEntity<?> findDocumentContent(@PathVariable("content-pk") Long contentPk) {
        ConfirmDocumentAndContentServiceResponse response = confirmDocumentService.findDocumentContent(contentPk);
        return ResponseEntity.ok(response);
    }
}
