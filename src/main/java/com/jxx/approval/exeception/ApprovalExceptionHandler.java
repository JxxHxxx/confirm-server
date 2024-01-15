package com.jxx.approval.exeception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApprovalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<?> handle() {
        log.info("");

        return null;
    }
}
