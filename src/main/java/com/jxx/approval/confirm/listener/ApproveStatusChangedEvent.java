package com.jxx.approval.confirm.listener;

import com.jxx.approval.confirm.domain.ConfirmStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
