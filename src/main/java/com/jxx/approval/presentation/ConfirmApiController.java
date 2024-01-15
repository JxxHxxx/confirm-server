package com.jxx.approval.presentation;

import com.jxx.approval.application.ApprovalLineService;
import com.jxx.approval.application.ConfirmService;
import com.jxx.approval.dto.request.ApproverEnrollForm;
import com.jxx.approval.dto.request.ConfirmCreateForm;
import com.jxx.approval.dto.request.ConfirmDocumentSearchCondition;
import com.jxx.approval.dto.request.ConfirmRaiseForm;
import com.jxx.approval.dto.response.*;
import com.jxx.approval.infra.ConfirmDocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConfirmApiController {

    private final ConfirmService confirmService;
    private final ApprovalLineService approvalLineService;
    //결재 문서 생성
    @PostMapping("/api/confirm-documents")
    public ResponseEntity<?> save(@RequestBody ConfirmCreateForm form) {
        confirmService.createConfirmDocument(form);
        return ResponseEntity.ok("생성");
    }

    @PostMapping("/test/confirm-documents")
    public ResponseEntity<?> saveAuto(@RequestParam("iter") int iter) {
        confirmService.createAuto(iter);
        return ResponseEntity.ok("생성");
    }

    //결재 문서 업로드

    // 결재 문서 검색
    @GetMapping("/api/confirm-documents")
    public ResponseEntity<?> searchConfirmDocument(@ModelAttribute ConfirmDocumentSearchCondition condition) {
        List<ConfirmDocumentServiceResponse> responses = confirmService.search(condition);
        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "검색 조회", responses));
    }

    // 결재 문서 PK 조회
    @GetMapping("/api/confirm-documents/{confirm-document-pk}")
    public ResponseEntity<ResponseResult<?>> readByPk(@PathVariable(value = "confirm-document-pk") Long confirmDocumentPk) {
        ConfirmDocumentServiceResponse response = confirmService.readByPk(confirmDocumentPk);

        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "결재 단건 조회", response));
    }

    // 결재 문서 상신
    @PostMapping("/api/confirm-documents/{confirm-document-pk}/raise")
    public ResponseEntity<?> raise(@PathVariable(name = "confirm-document-pk") Long confirmDocumentPk,
                                   @RequestBody ConfirmRaiseForm form) {
        ConfirmServiceResponse response = confirmService.raise(confirmDocumentPk, form);

        return ResponseEntity.ok(response);
    }

    // 결재 문서에 대한 결재자 등록
    @PostMapping("/api/confirm-documents/{confirm-document-pk}/approvals")
    public ResponseEntity<?> enrollApprovalLine(@PathVariable(name = "confirm-document-pk") Long confirmDocumentPk,
                                                @RequestBody List<ApproverEnrollForm> forms) {
        List<ApproverServiceResponse> responses = approvalLineService.enrollApprovers(forms, confirmDocumentPk);
        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "결재자 등록", responses));
    }

    // 결재 문서 승인
    @PatchMapping("/api/confirm-documents/{confirm-document-pk}/accept")
    public ResponseEntity<?> acceptConfirmDocument(@PathVariable(name = "confirm-document-pk") Long confirmDocumentPk) {

        return ResponseEntity.ok(null);
    }
    // 결재 문서 반려
    // 결재 문서 취소
    // 결재 문서 수정

}
