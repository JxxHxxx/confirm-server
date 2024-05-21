package com.jxx.approval.confirm.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.approval.common.client.SimpleRestClient;
import com.jxx.approval.confirm.domain.*;
import com.jxx.approval.confirm.dto.request.ConfirmStatusChangeRequest;
import com.jxx.approval.confirm.infra.ConfirmDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmDocumentEventListener {
    private final ConfirmDocumentRepository confirmDocumentRepository;

    @TransactionalEventListener(value = ApproveStatusChangedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(ApproveStatusChangedEvent event) {
        // 로깅 작업 해야함
        ConfirmDocument confirmDocument = confirmDocumentRepository.findWithContent(event.getConfirmDocumentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다."));

        if (confirmDocument.confirmStatusNotBelongIn(event.getConfirmStatus())) {
            throw new ConfirmDocumentException("승인자의 요청을 처리할 수 없습니다. 요청 종류 :" + event.getRequestType(), event.getApprovalLineId());
        }
    }

    @TransactionalEventListener(value = ConfirmStatusEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(ConfirmStatusEvent event) throws JsonProcessingException {
        log.info("cdp:{} eventType:{}", event.confirmDocumentId(), event.confirmStatusToChange());
        ConfirmDocument confirmDocument = confirmDocumentRepository.findWithContent(event.confirmDocumentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다."));


        SimpleRestClient simpleRestClient = new SimpleRestClient();

        switch (event.confirmStatusToChange()) {
            case ACCEPT -> {
                if (confirmDocument.anyApprovalNotAccepted()) {
                    log.warn("cdp:{} something wrong", confirmDocument.getPk());
                    throw new ConfirmDocumentException("잘못된 접근입니다. 관리자에게 문의하세요.");
                }
                // 결재 문서 상태 변경 및 최종 승인/반려 시간 지정
                confirmDocument.changeConfirmStatus(event.confirmStatusToChange());
                confirmDocument.setCompletedTime(event.completedTime());

                simpleRestClient.postForEntity("http://localhost:8080/api/vacations/{vacation-id}/vacation-status",
                        new ConfirmStatusChangeRequest("confirm-server", "APPROVED"),
                        String.class,
                        event.vacationId());
            }

            case REJECT -> {
                // TODO 여기 결재 반려/최종승신 이벤트와 트랜잭션 묶어줘야 함 위에도 마찬가지
                confirmDocument.changeConfirmStatus(event.confirmStatusToChange());
                simpleRestClient.postForEntity("http://localhost:8080/api/vacations/{vacation-id}/vacation-status",
                        new ConfirmStatusChangeRequest("confirm-server", "REJECT"),
                        String.class,
                        event.vacationId());
            }
        }

    }

}
