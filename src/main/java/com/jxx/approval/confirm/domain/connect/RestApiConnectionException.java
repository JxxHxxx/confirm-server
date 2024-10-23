package com.jxx.approval.confirm.domain.connect;

import lombok.Getter;

@Getter
public class RestApiConnectionException extends RuntimeException {
    private final RestApiConnResponseCode responseCode;
    private final String description;
    private int httpStatusCode; // 연동 API 에서 응답한 상태 코드
    private String confirmDocumentId;
    private String documentType;
    private String triggerType;

    public RestApiConnectionException(RestApiConnResponseCode restApiConnResponseCode) {
        super(restApiConnResponseCode.getDescription());
        this.responseCode = restApiConnResponseCode;
        this.description = restApiConnResponseCode.getDescription();
    }

    public RestApiConnectionException(RestApiConnResponseCode restApiConnResponseCode, int httpStatusCode) {
        super(restApiConnResponseCode.getDescription());
        this.responseCode = restApiConnResponseCode;
        this.description = restApiConnResponseCode.getDescription();
        this.httpStatusCode = httpStatusCode;
    }

    public RestApiConnectionException(RestApiConnResponseCode restApiConnResponseCode, Throwable cause) {
        super(cause);
        this.responseCode = restApiConnResponseCode;
        this.description = restApiConnResponseCode.getDescription();
    }
}
