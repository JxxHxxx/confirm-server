package com.jxx.approval.confirm.presentation;

import com.jxx.approval.confirm.domain.document.ConfirmDocumentException;
import com.jxx.approval.confirm.domain.line.ApprovalLineException;
import com.jxx.approval.confirm.domain.line.IllegalOrderMethodInvokeException;
import com.jxx.approval.confirm.dto.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ConfirmDocumentExceptionHandler {

    @ExceptionHandler(ConfirmDocumentException.class)
    public ResponseEntity<ResponseResult> handle(ConfirmDocumentException exception) {
        log.error("", exception);
        String data = null;
        String errorCode = exception.getErrorCode();
        if (Objects.nonNull(errorCode)) {
            data = errorCode;
        }

        return ResponseEntity
                .badRequest()
                .body(new ResponseResult<>(400, exception.getMessage(), data));
    }

    @ExceptionHandler(ApprovalLineException.class)
    public ResponseEntity<ResponseResult> handle(ApprovalLineException exception) {
        log.error("FAIL MSG : {} REQUESTER ID : {}", exception.getMessage(), exception.getApprovalId(), exception);
        return ResponseEntity
                .badRequest()
                .body(new ResponseResult<>(400, exception.getMessage(), null));
    }

    @ExceptionHandler(IllegalOrderMethodInvokeException.class)
    public ResponseEntity<ResponseResult> handle(IllegalOrderMethodInvokeException exception) {
        log.error("ERROR OCCUR : ", exception);
        return ResponseEntity
                .internalServerError()
                .body(new ResponseResult(500, "서버 에러입니다. 관리자에게 문의하십시오", null));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<?> handleHttpClientErrorException(HttpClientErrorException exception) {
        ResponseResult response = exception.getResponseBodyAs(ResponseResult.class);
        return ResponseEntity.badRequest().body(response);
    }
}

