package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.document.ConfirmStatus;
import com.jxx.approval.confirm.domain.document.DocumentType;
import com.jxx.approval.confirm.domain.line.ApprovalLineLifecycle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmDocumentServiceResponse {
    private Long pk;
    private String confirmDocumentId;
    private LocalDateTime createTime;
    private String companyId;
    private String departmentId;
    private String departmentName;
    private String createSystem;
    private ConfirmStatus confirmStatus;
    private LocalDateTime completedTime;
    private DocumentType documentType;
    private String requesterId;
    private String requesterName;
    private Long contentPk;
    private ApprovalLineLifecycle approvalLineLifecycle;
    private Map<String, Object> contents;
}
