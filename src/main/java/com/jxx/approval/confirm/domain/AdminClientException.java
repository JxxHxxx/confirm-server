package com.jxx.approval.confirm.domain;

import lombok.Getter;

@Getter
public class AdminClientException extends RuntimeException {
    public AdminClientException() {
    }

    public AdminClientException(String message) {
        super(message);
    }
}
