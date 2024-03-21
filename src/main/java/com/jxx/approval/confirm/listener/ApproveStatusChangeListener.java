package com.jxx.approval.confirm.listener;

import com.jxx.approval.confirm.domain.*;
import com.jxx.approval.confirm.infra.ConfirmDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class ApproveStatusChangeListener {
    private final ConfirmDocumentRepository confirmDocumentRepository;

    @EventListener(ApproveStatusChangedEvent.class)
    public void handle(ApproveStatusChangedEvent event) {
        // 로깅 작업 해야함
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(event.getConfirmDocumentPk())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다."));

        if (confirmDocument.confirmStatusNotBelongIn(event.getConfirmStatus())) {
            throw new ConfirmDocumentException("승인자의 요청을 처리할 수 없습니다. 요청 종류 :" + event.getRequestType(), event.getApprovalLineId());
        }
    }

    @EventListener(ConfirmStatusEvent.class)
    public void handle(ConfirmStatusEvent event) {
        log.info("cdp:{} eventType:{}", event.confirmDocumentPk(), event.confirmStatus());
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(event.confirmDocumentPk())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다."));

        if (confirmDocument.anyApprovalNotAccepted()) {
            log.warn("cdp:{} something wrong", confirmDocument.getPk());
            throw new ConfirmDocumentException("잘못된 접근입니다. 관리자에게 문의하세요.");
        }
        confirmDocument.changeConfirmStatus(event.confirmStatus());
    }

}
