package com.jxx.approval.confirm.dto.request;

import com.jxx.approval.confirm.domain.ConfirmStatus;
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
    private ConfirmStatus confirmStatus; // enum 을 String 이랑 비교하지 못하는 문제
    private String approvalId;
    private String requesterId;
    private LocalDate createDate;


}
