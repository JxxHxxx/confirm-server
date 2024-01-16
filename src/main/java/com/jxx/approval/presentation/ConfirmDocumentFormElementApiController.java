package com.jxx.approval.presentation;

import com.jxx.approval.application.ConfirmDocumentFormElementService;
import com.jxx.approval.dto.request.ConfirmDocumentElementForm;
import com.jxx.approval.dto.request.FormElementCreateRequest;
import com.jxx.approval.dto.response.ConfirmDocumentElementServiceResponse;
import com.jxx.approval.dto.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConfirmDocumentFormElementApiController {

    private final ConfirmDocumentFormElementService confirmDocumentFormElementService;

    @PostMapping("/admin/confirm-document-elements")
    public ResponseEntity<?> createElement(List<ConfirmDocumentElementForm> form) {
        List<ConfirmDocumentElementServiceResponse> response = confirmDocumentFormElementService.createElements(form);

        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "생성 완료", response));
    }

    //조회 다건
    @GetMapping("/admin/confirm-document-elements")
    public ResponseEntity<?> readElements() {
        List<ConfirmDocumentElementServiceResponse> response = confirmDocumentFormElementService.findElements();

        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "조회 완료", response));
    }

    @PostMapping("/admin/confirm-document-form-elements")
    public ResponseEntity<?> createFormElement(FormElementCreateRequest request) {
        confirmDocumentFormElementService.createFormElement(request);

        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "결재 문서 양식 생성 완료", null));
    }
}
