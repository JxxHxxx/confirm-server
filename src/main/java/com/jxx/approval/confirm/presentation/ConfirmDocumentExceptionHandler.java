package com.jxx.approval.confirm.presentation;

import com.jxx.approval.confirm.domain.connect.RestApiConnectionException;
import com.jxx.approval.confirm.domain.document.ConfirmDocumentException;
import com.jxx.approval.confirm.domain.line.ApprovalLineException;
import com.jxx.approval.confirm.domain.line.IllegalOrderMethodInvokeException;
import com.jxx.approval.confirm.dto.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.jxx.approval.confirm.domain.connect.RestApiConnResponseCode.*;

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

    @ExceptionHandler(RestApiConnectionException.class)
    public ResponseEntity<?> handle(RestApiConnectionException exception) {
        String description = exception.getDescription();

        switch (exception.getResponseCode()) {
            case RCF01 -> {
                log.error("RC:{} DESC:{}",RCF01.name(), RCF01.getDescription(), exception);
                Map<String, Object> response = new HashMap<>();
                response.put("errCode", RCF01.name());
                response.put("thirdPartyResponseHttpStatusCode", exception.getHttpStatusCode());
                return ResponseEntity.badRequest().body(new ResponseResult<>(400, description, response));
            }
            case RCF02 -> {
                log.error("==========SERIOUS==========\n" +
                        "RC:{} DESC:{} \n " +
                          "============================",RCF02.name(), RCF02.getDescription(), exception);
                Map<String, Object> response = new HashMap<>();
                response.put("errCode", RCF02.name());
                return ResponseEntity.badRequest().body(new ResponseResult<>(500, description, response));
            }
            case RCF90 -> {
                log.error("RC:{} DESC:{}",RCF90.name(), RCF90.getDescription(), exception);
                return ResponseEntity.internalServerError().body(new ResponseResult<>(500, description, null));
            }
            case RCF99 -> {
                log.error("RC:{} DESC:{}",RCF99.name(), RCF99.getDescription(), exception);
                return ResponseEntity.badRequest().body(new ResponseResult<>(400, description, null));
            }
            // 컴파일을 위한 코드로 설계상 RCF, RCS 모두 아래 케이스를 타면 안됨
            default -> {
                log.error("==========SERIOUS==========\n" +
                        "RC:{} DESC:{} 시스템 오류입니다. 개발자 확인이 필요하며 해당 케이스를 타지 않도록 해야 합니다.\n" +
                        "만약 RCS 케이스라면 이 예외 핸들러에서 처리하면 안됩니다. \n" +
                        "============================",RCF02.name(), RCF02.getDescription(), exception);
                return ResponseEntity.internalServerError().body(new ResponseResult<>(500, description, exception.getResponseCode()));
            }
        }
    }
}

