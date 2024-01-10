package com.jxx.approval.dto.request;

import com.jxx.approval.domain.ConfirmStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
//### 결재문서 검색(결재 문서 ID, 결재 상태, 결재자, 요청자, 생성일자)
public class ConfirmDocumentSearchCondition {

    private String confirmDocumentId;
    private ConfirmStatus confirmStatus;
    private String approvalId;
    private String requesterId;
    private LocalDate createDate;


}
