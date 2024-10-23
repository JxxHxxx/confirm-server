package com.jxx.approval.confirm.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.approval.confirm.application.ConfirmDocumentRestApiAdapterService;
import com.jxx.approval.confirm.domain.connect.RestApiConnResponseCode;
import com.jxx.approval.confirm.domain.document.ConfirmDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmDocumentEventListener {

    private final ConfirmDocumentRestApiAdapterService confirmDocumentRestApiAdapterService;

    // 결재 문서 상신 처리가 완료 된 후, 처리
    // 결재 문서 상신 처리 후, 트랜잭션 종료 후
    // 여기서는 I/O 로직
    @Async
    @TransactionalEventListener(value = ConfirmDocumentRaiseEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handleRaiseEvent(ConfirmDocumentRaiseEvent event) throws JsonProcessingException {
        ConfirmDocument confirmDocument = event.confirmDocument();
        log.info("START >> CDID:{} TRG:RAISE", confirmDocument.getConfirmDocumentId());
        RestApiConnResponseCode responseCode = confirmDocumentRestApiAdapterService.call(confirmDocument, "RAISE");
        log.info("END   >> CDID:{} TRG:RAISE RC:{}", confirmDocument.getConfirmDocumentId(), responseCode);
    }

    /**
     * 결재 문서 최종 승인 시 발생하는 이벤트 처리 로직
     * 외부 통신이 발생할 수 있다. SYNC 권장
     **/
    @TransactionalEventListener(value = ConfirmDocumentFinalAcceptDecisionEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handleConfirmDocumentFinalAcceptDecisionEvent(ConfirmDocumentFinalAcceptDecisionEvent event) throws JsonProcessingException {
        ConfirmDocument confirmDocument = event.confirmDocument();
        log.info("START >> CDID:{} TRG:FINAL_ACCEPT", confirmDocument.getConfirmDocumentId());
        RestApiConnResponseCode responseCode = confirmDocumentRestApiAdapterService.call(confirmDocument, "FINAL_ACCEPT");
        log.info("END   >> CDID:{} TRG:FINAL_ACCEPT RC:{}", confirmDocument.getConfirmDocumentId(), responseCode);
    }

    @TransactionalEventListener(value = ConfirmDocumentRejectDecisionEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ConfirmDocumentRejectDecisionEvent event) throws JsonProcessingException {
        ConfirmDocument confirmDocument = event.confirmDocument();
        log.info("START >> CDID:{} TRG:REJECT", confirmDocument.getConfirmDocumentId());
        RestApiConnResponseCode responseCode = confirmDocumentRestApiAdapterService.call(confirmDocument, "REJECT");
        log.info("END   >> CDID:{} TRG:REJECT RC:{}", confirmDocument.getConfirmDocumentId(), responseCode);
    }
}
