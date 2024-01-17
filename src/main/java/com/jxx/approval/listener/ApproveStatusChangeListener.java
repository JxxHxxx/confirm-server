package com.jxx.approval.listener;

import com.jxx.approval.domain.ConfirmDocument;
import com.jxx.approval.domain.ConfirmDocumentException;
import com.jxx.approval.infra.ConfirmDocumentRepository;
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
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(event.getConfirmDocumentPk())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다."));

        if (confirmDocument.confirmStatusNotBelongIn(event.getConfirmStatus())) {
            throw new ConfirmDocumentException("승인자의 요청을 처리할 수 없습니다. 요청 종류 :" + event.getRequestType(), event.getApprovalLineId());

        }
    }
}
