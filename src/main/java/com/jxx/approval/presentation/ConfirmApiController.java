package com.jxx.approval.presentation;

import com.jxx.approval.application.ApprovalLineService;
import com.jxx.approval.application.ConfirmService;
import com.jxx.approval.dto.request.ApproverEnrollForm;
import com.jxx.approval.dto.request.ConfirmDocumentSearchCondition;
import com.jxx.approval.dto.request.ConfirmRaiseForm;
import com.jxx.approval.dto.response.*;
import com.jxx.approval.infra.ConfirmDocumentSearchRepository;
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
    private final ConfirmDocumentSearchRepository searchRepository;

    //결재 문서 생성

    // 결재 문서 검색
    @GetMapping("/api/confirm-documents")
    public ResponseEntity<?> searchConfirmDocument(@ModelAttribute ConfirmDocumentSearchCondition condition) {
        confirmService.search(condition);
        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "검색 조회", null));
    }

    // 결재 문서 PK 조회
    @GetMapping("/api/confirm-documents/{confirm-document-pk}")
    public ResponseEntity<ResponseResult<?>> readByPk(@PathVariable(value = "confirm-document-pk") Long confirmDocumentPk) {
        ConfirmServiceDto response = confirmService.readByPk(confirmDocumentPk);

        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "결재 단건 조회", response));
    }

    @GetMapping("/api/confirm-documents/{confirm-document-pk}/test")
    public ResponseEntity<ResponseResult<?>> readByPkV2(@PathVariable(value = "confirm-document-pk") Long confirmDocumentPk) {
        ConfirmServiceDto response = searchRepository.select(confirmDocumentPk);

        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "결재 단건 조회", response));
    }

    // 결재 문서 상신
    @PostMapping("/api/confirm-documents/{confirm-document-pk}/raise")
    public ResponseEntity<?> raise(@PathVariable(name = "confirm-document-pk") Long confirmDocumentPk,
                                   @RequestBody ConfirmRaiseForm form) {
        ConfirmServiceResponse response = confirmService.raise(confirmDocumentPk, form);

        return ResponseEntity.ok(response);
    }

    // 결재 문서 상신
    @PostMapping("/api/confirm-documents/raise")
    public ResponseEntity<?> raise(@RequestParam(name = "cdid") String confirmDocumentId,
                                   @RequestBody ConfirmRaiseForm form) {
        ConfirmRaiseServiceResponse response = confirmService.raise(confirmDocumentId, form);

        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "결재 문서 상신 요청 결과", response));
    }
    // 결재 문서 승인
    // 결재 문서 반려
    // 결재 문서 취소
    // 결재 문서 수정

    // 결재 문서에 대한 결재자 등록
    @PostMapping("/api/confirm-documents/{confirm-document-pk}/approvals")
    public ResponseEntity<?> enrollApprovalLine(@PathVariable(name = "confirm-document-pk") Long confirmDocumentPk,
                                    @RequestBody List<ApproverEnrollForm> forms) {
        List<ApproverServiceResponse> responses = approvalLineService.enrollApprovers(forms, confirmDocumentPk);
        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "결재자 등록", responses));
    }
}
