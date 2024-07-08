package com.jxx.approval.confirm.listener;

import com.jxx.approval.confirm.domain.document.ConfirmStatus;
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

    private String confirmDocumentId;
    private String approvalLineId;
    private String requestType;
    private List<ConfirmStatus> confirmStatus;


    public static ApproveStatusChangedEvent acceptEvent(String  confirmDocumentId, String approvalLineId) {
        return new ApproveStatusChangedEvent(confirmDocumentId, approvalLineId, "결재 승인", ConfirmStatus.acceptPossibleOfApproval);
    }


    public static ApproveStatusChangedEvent rejectEvent(String confirmDocumentId, String approvalLineId) {
        return new ApproveStatusChangedEvent(confirmDocumentId, approvalLineId, "결재 반려", ConfirmStatus.rejectPossibleOfApproval);
    }
}
