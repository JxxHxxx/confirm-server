package com.jxx.approval.application;

import com.jxx.approval.domain.ConfirmDocument;
import com.jxx.approval.domain.ConfirmDocumentRepository;
import com.jxx.approval.dto.request.ApprovalForm;
import com.jxx.approval.dto.response.ConfirmDocumentServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jxx.approval.domain.ConfirmStatus.*;

@Service
@RequiredArgsConstructor
public class ConfirmService {

    private final ConfirmDocumentRepository confirmDocumentRepository;

    @Transactional
    public ConfirmDocumentServiceResponse raise(String confirmDocumentId, ApprovalForm form) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByConfirmDocumentId(confirmDocumentId)
                .orElseThrow(() -> new IllegalArgumentException());
        confirmDocument.changeConfirmStatus(RAISE);
        return new ConfirmDocumentServiceResponse(confirmDocumentId, form.approvalId(), form.requesterId());
    }
}
