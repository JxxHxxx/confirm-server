package com.jxx.approval.confirm.dto.response;

import com.jxx.approval.confirm.domain.ApproveStatus;
import com.jxx.approval.confirm.domain.ConfirmStatus;
import com.jxx.approval.confirm.domain.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor // MYbatis 가 기본생성자씀
@AllArgsConstructor
public class ConfirmDocumentWithApprovalLineResponse {
    private Long pk;
    private String confirmDocumentId;
    private String companyId;
    private String departmentId;
    private String createSystem;
    private ConfirmStatus confirmStatus;
    private DocumentType documentType;
    private String requesterId;
    private LocalDateTime createTime;
    private Long confirmDocumentContentPk;
    private Map<String, Object> contents;
    private Long approvalLinePk;
    private String approvalId;
    private ApproveStatus approvalStatus;
    private LocalDateTime approvalTime;
}
