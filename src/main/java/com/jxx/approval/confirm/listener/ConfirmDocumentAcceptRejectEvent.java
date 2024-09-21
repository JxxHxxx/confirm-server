package com.jxx.approval.confirm.listener;

import com.jxx.approval.confirm.domain.document.ConfirmDocument;
import com.jxx.approval.confirm.domain.document.ConfirmStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 결재자의 승인/반려 status 를 변경하려고 할 때 발생하는 이벤트를 처리합니다.
 */

@Getter
@RequiredArgsConstructor
public class ConfirmDocumentAcceptRejectEvent {

    private final ConfirmDocument confirmDocument;
    /**
     * 변경하려는 결재 문서 상태
     * ex) 승인으로 변경하려면 ConfirmStatus.ACCEPT
     **/
    private final ConfirmStatus toConfirmStatus;


    public static ConfirmDocumentAcceptRejectEvent acceptEvent(ConfirmDocument ConfirmDocument) {
        return new ConfirmDocumentAcceptRejectEvent(ConfirmDocument, ConfirmStatus.ACCEPT);
    }

    public static ConfirmDocumentAcceptRejectEvent rejectEvent(ConfirmDocument ConfirmDocument) {
        return new ConfirmDocumentAcceptRejectEvent(ConfirmDocument, ConfirmStatus.REJECT);
    }

    public String validateSuccessMessage() {
        return "[VALID:SUCCESS:acceptable/rejectable]{confirmDocumentId=" + confirmDocument.getConfirmDocumentId() + ", toConfirmStatus=" + toConfirmStatus + "}";
    }

    public String validateFailMessage() {
        List<ConfirmStatus> possibleGroup;
        if (toConfirmStatus.equals(ConfirmStatus.REJECT)) {
            possibleGroup = ConfirmStatus.REJECT_POSSIBLE_STATUS;
        } else if (toConfirmStatus.equals(ConfirmStatus.ACCEPT)) {
            possibleGroup = ConfirmStatus.ACCEPT_POSSIBLE_STATUS;
        } else {
            possibleGroup = Collections.emptyList();
        }

        return "[VALID:FAIL:acceptable/rejectable]" +
                "{confirmDocumentId=" + confirmDocument.getConfirmDocumentId() + " toConfirmStatus=" + toConfirmStatus +
                "\n CAUSE: this request can only possible when confirmStatus in " + possibleGroup + " but now confirmStatus is " + confirmDocument.getConfirmStatus() + "}";
    }
}
