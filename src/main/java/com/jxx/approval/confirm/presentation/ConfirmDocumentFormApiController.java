package com.jxx.approval.confirm.presentation;

import com.jxx.approval.confirm.application.ConfirmDocumentFormService;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentElementRequest;
import com.jxx.approval.confirm.dto.request.ElementPair;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentFormRequest;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentElementServiceResponse;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentFormElementResponse;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentFormResponse;
import com.jxx.approval.confirm.dto.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConfirmDocumentFormApiController {

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

    @GetMapping("/api/confirm-document-forms")
    public ResponseEntity<?> getConfirmDocumentForms(@RequestParam("companyId") String companyId) {
        List<ConfirmDocumentFormResponse> response =  confirmDocumentFormService.findConfirmDocumentForms(companyId);
        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "문서 양식 조회 완료", response));
    }

    @GetMapping("/api/confirm-document-forms/{confirm-document-form-id}/elements")
    public ResponseEntity<?> getConfirmDocumentFormElements(@RequestParam("companyId") String companyId, @PathVariable("confirm-document-form-id") String confirmDocumentFormId) {
        List<ElementPair> responses =  confirmDocumentFormService.findConfirmDocumentFormElement(companyId, confirmDocumentFormId);
        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "문서 양식 요소 조회 완료", responses));
    }

    @GetMapping("/api/v2/confirm-document-forms/{confirm-document-form-id}/elements")
    public ResponseEntity<?> getConfirmDocumentFormElementsV2(@RequestParam("companyId") String companyId,
                                                              @PathVariable("confirm-document-form-id") String confirmDocumentFormId) {
        List<ConfirmDocumentFormElementResponse> responses =  confirmDocumentFormService.findConfirmDocumentFormElementV2(companyId, confirmDocumentFormId);
        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "문서 양식 요소 조회 V2", responses));
    }
}
