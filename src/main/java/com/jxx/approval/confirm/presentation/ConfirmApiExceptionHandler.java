package com.jxx.approval.confirm.presentation;

import com.jxx.approval.confirm.dto.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice(basePackages = {"com.jxx.approval.confirm"})
public class ConfirmApiExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<?> handleHttpClientErrorException(HttpClientErrorException exception) {
        ResponseResult response = exception.getResponseBodyAs(ResponseResult.class);
        return ResponseEntity.badRequest().body(response);
    }
}
