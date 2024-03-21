package com.jxx.approval.confirm.listener;

import com.jxx.approval.confirm.domain.ConfirmStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 결재자의 승인/반려 status 를 변경하려고 할 때 발생하는 이벤트를 처리합니다.
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApproveStatusChangedEvent {

    private Long confirmDocumentPk;
    private String approvalLineId;
    private String requestType;
    private List<ConfirmStatus> confirmStatus;


    public static ApproveStatusChangedEvent acceptEvent(Long confirmDocumentPk, String approvalLineId) {
        return new ApproveStatusChangedEvent(confirmDocumentPk, approvalLineId, "결재 승인", ConfirmStatus.acceptPossibleOfApproval);
    }


    public static ApproveStatusChangedEvent rejectEvent(Long confirmDocumentPk, String approvalLineId) {
        return new ApproveStatusChangedEvent(confirmDocumentPk, approvalLineId, "결재 승인", ConfirmStatus.rejectPossibleOfApproval);
    }
}
