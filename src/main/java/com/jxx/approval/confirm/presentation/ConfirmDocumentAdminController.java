package com.jxx.approval.confirm.presentation;

import com.jxx.approval.confirm.application.ConfirmDocumentFormService;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentElementRequest;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentFormRequest;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentFormSearchCond;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentElementServiceResponse;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentFormResponse;
import com.jxx.approval.confirm.dto.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ConfirmDocumentAdminController {

    private final ConfirmDocumentFormService confirmDocumentFormService;


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
}
