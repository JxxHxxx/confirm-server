package com.jxx.approval.exeception;

import com.jxx.approval.domain.ConfirmDocumentException;
import com.jxx.approval.dto.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ConfirmDocumentExceptionHandler {

    @ExceptionHandler(ConfirmDocumentException.class)
    public ResponseEntity<ResponseResult> handle(ConfirmDocumentException exception) {
        log.info("FAIL MSG : {} REQUESTER ID : {}", exception.getMessage(), exception.getRequesterId(), exception);
        return ResponseEntity.badRequest().body(new ResponseResult<>(400, exception.getMessage(), null));

    }
}

