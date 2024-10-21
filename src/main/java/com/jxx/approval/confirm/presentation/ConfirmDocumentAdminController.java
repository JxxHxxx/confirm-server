package com.jxx.approval.confirm.presentation;

import com.jxx.approval.confirm.application.AdminConfirmService;
import com.jxx.approval.confirm.application.ConfirmDocumentFormService;
import com.jxx.approval.confirm.dto.request.ConfirmConnectionApiRequest;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentElementRequest;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentFormRequest;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentFormSearchCond;
import com.jxx.approval.confirm.dto.response.ApprovalLineServiceDto;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentElementServiceResponse;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentFormResponse;
import com.jxx.approval.confirm.dto.response.ResponseResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
public class ConfirmDocumentAdminController {

    private final ConfirmDocumentFormService confirmDocumentFormService;
    private final AdminConfirmService adminConfirmService;


    @PostMapping("/admin/confirm-document-forms")
    public ResponseEntity<?> createForm(@RequestBody ConfirmDocumentFormRequest request) {
        confirmDocumentFormService.createForm(request);

        return ResponseEntity.ok(null);
    }

    // 회사 코드 추가해야 함
    @PostMapping("/admin/confirm-document-forms/{confirm-document-form-id}/elements")
    public ResponseEntity<?> createElements(@PathVariable("confirm-document-form-id") String confirmDocumentFormId, @RequestBody ConfirmDocumentElementRequest request) {
        List<ConfirmDocumentElementServiceResponse> response = confirmDocumentFormService.createElements(confirmDocumentFormId, request);

        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "생성 완료", response));
    }

    // 결재문서 양식 검색 API
    @GetMapping("/admin/confirm-document-forms")
    public ResponseEntity<?> searchConfirmDocumentForms(@ModelAttribute ConfirmDocumentFormSearchCond searchCond) {
        List<ConfirmDocumentFormResponse> responses = confirmDocumentFormService.searchConfirmDocumentForms(searchCond);
        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "문서 양식 검색", responses));
    }

    @GetMapping("/admin/confirm-documents/{confirm-document-id}/approval-lines")
    public ResponseEntity<?> findApprovalLines(@PathVariable("confirm-document-id") String confirmDocumentId) {
        List<ApprovalLineServiceDto> responses = adminConfirmService.findApprovalLinesBy(confirmDocumentId);
        return ResponseEntity.ok(new ResponseResult<>(OK.value(), "관리자용 결재선 조회", responses));
    }

    /**
     * 결재 연동 서비스 - 결재 문서의 특정 상태로 변경되면 매핑된 API 가 호출되도록 한다.
     * 연동 API 등록
     **/
    @PostMapping("/admin/confirm-documents/mapping-api")
    public ResponseEntity<?> mappingApi(@RequestBody ConfirmConnectionApiRequest request) {
        adminConfirmService.mappingApi(request);
        return ResponseEntity.status(201).body(null);
    }
}
