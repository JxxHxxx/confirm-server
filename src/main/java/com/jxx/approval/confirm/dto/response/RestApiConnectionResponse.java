package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.document.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestApiConnectionResponse {
    private Long connectionPk;
    private String description;
    private String scheme;
    private String host;
    private int port;
    private String methodType;
    private String path;
    private String triggerType;
    private DocumentType documentType;
    private LocalDateTime createDateTime;
    private String requesterId;
}
