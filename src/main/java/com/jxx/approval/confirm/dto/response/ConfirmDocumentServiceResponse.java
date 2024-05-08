package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.ConfirmStatus;
import com.jxx.approval.confirm.domain.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmDocumentServiceResponse {
    private Long pk;
    private String confirmDocumentId;
    private String companyId;
    private String departmentId;
    private String createSystem;
    private ConfirmStatus confirmStatus;
    private DocumentType documentType;
    private String requesterId;
    private Map<String, Object> contents;
}
