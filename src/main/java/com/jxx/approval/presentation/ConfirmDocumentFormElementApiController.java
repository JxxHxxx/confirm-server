package com.jxx.approval.presentation;

import com.jxx.approval.application.ConfirmDocumentFormElementService;
import com.jxx.approval.dto.request.ConfirmDocumentElementForm;
import com.jxx.approval.dto.request.FormElementCreateRequest;
import com.jxx.approval.dto.response.ConfirmDocumentElementServiceResponse;
import com.jxx.approval.dto.response.ContentServiceResponse;
import com.jxx.approval.dto.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConfirmDocumentFormElementApiController {

    private final ConfirmDocumentFormElementService formElementService;

    @PostMapping("/admin/confirm-document-elements")
    public ResponseEntity<?> createElement(@RequestBody List<ConfirmDocumentElementForm> form) {
        List<ConfirmDocumentElementServiceResponse> response = formElementService.createElements(form);

        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "생성 완료", response));
    }

    //조회 다건
    @GetMapping("/admin/confirm-document-elements")
    public ResponseEntity<?> readElements() {
        List<ConfirmDocumentElementServiceResponse> response = formElementService.findElements();

        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "조회 완료", response));
    }

    @PostMapping("/admin/confirm-document-form-elements")
    public ResponseEntity<?> createFormElement(@RequestBody FormElementCreateRequest request) {
        formElementService.createFormElement(request);

        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "결재 문서 양식 생성 완료", null));
    }

    @GetMapping("/api/confirm-document-forms/{confirm-document-form-pk}")
    public ResponseEntity<?> readFormElements(@PathVariable("confirm-document-form-pk") Long documentFormPk) {
        List<ContentServiceResponse> responses = formElementService.readFormElement(documentFormPk);

        return ResponseEntity.ok(new ResponseResult<>(200, "양식에 들어있는 요소", responses));
    }
}
