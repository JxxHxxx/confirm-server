package com.jxx.approval.presentation;

import com.jxx.approval.application.ConfirmService;
import com.jxx.approval.dto.request.ApprovalForm;
import com.jxx.approval.dto.response.ConfirmDocumentServiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConfirmApiController {

    private final ConfirmService confirmService;

    //consumes
    @PostMapping(value = "/api/confirm-documents/{confirm-document-id}/raise")
    public ResponseEntity<?> raise(@PathVariable(name = "confirm-document-id") String confirmDocumentId, @RequestBody ApprovalForm form) {
        ConfirmDocumentServiceResponse response = confirmService.raise(confirmDocumentId, form);

        return ResponseEntity.ok(response);
    }

}
