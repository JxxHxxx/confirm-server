package com.jxx.approval.application;

import com.jxx.approval.domain.*;
import com.jxx.approval.dto.request.ConfirmCreateForm;
import com.jxx.approval.dto.request.ConfirmDocumentSearchCondition;
import com.jxx.approval.dto.request.ConfirmRaiseForm;
import com.jxx.approval.dto.request.Document;
import com.jxx.approval.dto.response.ConfirmRaiseServiceResponse;
import com.jxx.approval.dto.response.ConfirmServiceDto;
import com.jxx.approval.dto.response.ConfirmServiceResponse;
import com.jxx.approval.infra.ApprovalLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jxx.approval.domain.ConfirmStatus.*;

@Service
@RequiredArgsConstructor
public class ConfirmService {

    private final ConfirmDocumentRepository confirmDocumentRepository;
    private final ApprovalLineRepository approvalLineRepository;
    @Transactional
    public void createConfirmDocument(ConfirmCreateForm form) {
        Document document = new Document(form.confirmDocument(), form.documentType());
        Requester requester = new Requester(form.companyId(), form.departmentId(), form.requesterId());

        ConfirmDocument confirmDocument = ConfirmDocument.builder()
                .document(document)
                .requester(requester)
                .createSystem(form.createSystem())
                .confirmStatus(CREATE)
                .build();

        confirmDocumentRepository.save(confirmDocument);
    }

    public ConfirmServiceDto readByPk(Long confirmDocumentPk) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(confirmDocumentPk)
                .orElseThrow(() -> new IllegalArgumentException("없어요~"));

        return toConfirmServiceDto(confirmDocument);
    }

    public List<ConfirmServiceDto> readAll() {
        List<ConfirmDocument> confirmDocuments = confirmDocumentRepository.findAll();

        return confirmDocuments.stream()
                .map(confirmDocument -> toConfirmServiceDto(confirmDocument))
                .toList();
    }

    @Transactional
    public ConfirmServiceResponse raise(Long confirmDocumentPk, ConfirmRaiseForm form) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(confirmDocumentPk)
                .orElseThrow(() -> new IllegalArgumentException());

        confirmDocument.checkDocumentRequester(form.requesterId());
        confirmDocument.verifyWhetherRiseIsPossible();

        confirmDocument.changeConfirmStatus(RAISE);
        return new ConfirmServiceResponse(confirmDocumentPk, form.requesterId());
    }

    @Transactional
    public ConfirmRaiseServiceResponse raise(String confirmDocumentId, ConfirmRaiseForm form) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByDocumentConfirmDocumentId(confirmDocumentId)
                .orElseThrow(() -> new IllegalArgumentException());

        confirmDocument.checkDocumentRequester(form.requesterId());
        confirmDocument.verifyWhetherRiseIsPossible();

        confirmDocument.changeConfirmStatus(RAISE);
        return new ConfirmRaiseServiceResponse(confirmDocument.getPk(), confirmDocument.requesterId(), confirmDocument.getConfirmStatus());
    }

    private static ConfirmServiceDto toConfirmServiceDto(ConfirmDocument confirmDocument) {
        return new ConfirmServiceDto(
                confirmDocument.getPk(),
                confirmDocument.getDocument().getConfirmDocumentId(),
                confirmDocument.getRequester().getCompanyId(),
                confirmDocument.getRequester().getDepartmentId(),
                confirmDocument.getCreateSystem(),
                confirmDocument.getConfirmStatus(),
                confirmDocument.getDocument().getDocumentType(),
                confirmDocument.getRequester().getRequesterId());
    }


    //mybatis 써야 됨
    public void search(ConfirmDocumentSearchCondition condition) {

    }
}
