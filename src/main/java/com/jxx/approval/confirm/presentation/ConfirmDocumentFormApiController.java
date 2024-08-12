package com.jxx.approval.confirm.presentation;

import com.jxx.approval.confirm.application.ConfirmDocumentFormService;
import com.jxx.approval.confirm.dto.request.ElementPair;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentFormElementResponse;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentFormResponse;
import com.jxx.approval.confirm.dto.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConfirmDocumentFormApiController {

    private final ConfirmDocumentFormService confirmDocumentFormService;

    @GetMapping("/api/confirm-document-forms")
    public ResponseEntity<?> getConfirmDocumentForms(@RequestParam("companyId") String companyId) {
        List<ConfirmDocumentFormResponse> response =  confirmDocumentFormService.findConfirmDocumentForms(companyId);
        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "문서 양식 조회 완료", response));
    }

    @GetMapping("/api/confirm-document-forms/{confirm-document-form-id}/elements")
    public ResponseEntity<?> getElements(@RequestParam("companyId") List<String> companyId,
                                         @PathVariable("confirm-document-form-id") String confirmDocumentFormId) {
        List<ConfirmDocumentFormElementResponse> responses =  confirmDocumentFormService.findConfirmDocumentFormElements(companyId, confirmDocumentFormId);
        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "문서 양식 요소 조회", responses));
    }
}
