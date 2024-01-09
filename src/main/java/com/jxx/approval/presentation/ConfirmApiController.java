package com.jxx.approval.presentation;

import com.jxx.approval.application.ApproverService;
import com.jxx.approval.application.ConfirmService;
import com.jxx.approval.dto.request.ApproverEnrollForm;
import com.jxx.approval.dto.request.ConfirmRaiseForm;
import com.jxx.approval.dto.response.*;
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
    private final ApproverService approverService;

    @GetMapping("/api/confirm-documents")
    public ResponseEntity<ResponseResult<ConfirmReadAllResponse>> readAll() {
        List<ConfirmServiceDto> serviceResponses = confirmService.readAll();
        List<ConfirmReadAllResponse> response = serviceResponses.stream()
                .map(serviceResponse -> serviceResponse.confirmDocumentReadAllResponse())
                .toList();

        return ResponseEntity.ok(new ResponseResult(HttpStatus.OK.value(), "결재 문서 다건 조회", response));
    }

    @GetMapping("/api/confirm-documents/{confirm-document-pk}")
    public ResponseEntity<ResponseResult<?>> readByPk(@PathVariable(value = "confirm-document-pk") Long confirmDocumentPk) {
        ConfirmServiceDto response = confirmService.readByPk(confirmDocumentPk);

        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "결재 단건 조회", response));
    }

    @PostMapping("/api/confirm-documents/{confirm-document-pk}/raise")
    public ResponseEntity<?> raise(@PathVariable(name = "confirm-document-pk") Long confirmDocumentPk,
                                   @RequestBody ConfirmRaiseForm form) {
        ConfirmServiceResponse response = confirmService.raise(confirmDocumentPk, form);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/confirm-documents/raise")
    public ResponseEntity<?> raise(@RequestParam(name = "cdid") String confirmDocumentId,
                                   @RequestBody ConfirmRaiseForm form) {
        ConfirmRaiseServiceResponse response = confirmService.raise(confirmDocumentId, form);

        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "결재 문서 상신 요청 결과", response));
    }

    @PostMapping("/api/confirm-documents/{confirm-document-pk}/approvers")
    public ResponseEntity<?> enroll(@PathVariable(name = "confirm-document-pk") Long confirmDocumentPk,
                                    @RequestBody List<ApproverEnrollForm> forms) {
        List<ApproverServiceResponse> responses = approverService.enrollApprovers(forms, confirmDocumentPk);
        return ResponseEntity.ok(new ResponseResult<>(HttpStatus.OK.value(), "결재자 등록", responses));
    }

}
