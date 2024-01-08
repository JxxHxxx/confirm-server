package com.jxx.approval.application;

import com.jxx.approval.domain.*;
import com.jxx.approval.dto.request.ConfirmCreateForm;
import com.jxx.approval.dto.request.ConfirmRaiseForm;
import com.jxx.approval.dto.request.Document;
import com.jxx.approval.dto.response.ConfirmServiceDto;
import com.jxx.approval.dto.response.ConfirmServiceResponse;
import com.jxx.approval.infra.ApproverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jxx.approval.domain.ConfirmStatus.*;

@Service
@RequiredArgsConstructor
public class ConfirmService {

    private final ConfirmDocumentRepository confirmDocumentRepository;
    private final ApproverRepository approverRepository;
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


        confirmDocument.changeConfirmStatus(RAISE);
        return new ConfirmServiceResponse(confirmDocumentPk, form.requesterId());
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

}
