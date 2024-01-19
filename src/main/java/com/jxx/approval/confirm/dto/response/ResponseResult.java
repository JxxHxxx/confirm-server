package com.jxx.approval.confirm.dto.response;

import lombok.Getter;

@Getter
public class ResponseResult<T> {
    private final Integer status;
    private final String message;
    private final T response;

    public ResponseResult(Integer status, String message, T response) {
        this.status = status;
        this.message = message;
        this.response = response;
    }
}
