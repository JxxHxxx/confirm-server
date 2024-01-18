package com.jxx.approval.application;

import com.jxx.approval.domain.*;
import com.jxx.approval.dto.request.*;
import com.jxx.approval.dto.response.ConfirmDocumentServiceResponse;
import com.jxx.approval.dto.response.ConfirmServiceResponse;
import com.jxx.approval.infra.*;
import com.jxx.approval.utils.ConfirmDocumentGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxx.approval.domain.ConfirmDocumentException.*;
import static com.jxx.approval.domain.ConfirmStatus.*;

@Service
@RequiredArgsConstructor
public class ConfirmDocumentService {

    private final ConfirmDocumentRepository confirmDocumentRepository;
    private final ConfirmDocumentMapper confirmDocumentMapper;
    private final ConfirmDocumentContentRepository contentRepository;

    @Transactional
    public void createAuto(int iter) {
        List<ConfirmDocument> confirmDocuments = new ArrayList<>();
        for (int i = 0; i < iter; i++) {
            ConfirmDocument confirmDocument = ConfirmDocumentGenerator.execute();
            confirmDocuments.add(confirmDocument);
        }

        confirmDocumentRepository.saveAll(confirmDocuments);
    }

    @Transactional
    public void createConfirmDocument(ConfirmCreateForm form) {
        Document document = new Document(form.documentType());
        Requester requester = new Requester(form.companyId(), form.departmentId(), form.requesterId());

        ConfirmDocument confirmDocument = ConfirmDocument.builder()
                .document(document)
                .requester(requester)
                .createSystem(form.createSystem())
                .build();

        confirmDocumentMapper.save(confirmDocument);
    }

    public ConfirmDocumentServiceResponse readByPk(Long confirmDocumentPk) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(confirmDocumentPk)
                .orElseThrow(() -> new IllegalArgumentException("없어요~"));

        return toConfirmServiceDto(confirmDocument);
    }

    public List<ConfirmDocumentServiceResponse> readAll() {
        List<ConfirmDocument> confirmDocuments = confirmDocumentRepository.findAll();

        return confirmDocuments.stream()
                .map(confirmDocument -> toConfirmServiceDto(confirmDocument))
                .toList();
    }

    @Transactional
    public ConfirmServiceResponse raise(Long confirmDocumentPk, ConfirmRaiseForm form) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(confirmDocumentPk)
                .orElseThrow(() -> new IllegalArgumentException());

        Requester raiseRequester = new Requester(form.companyId(), form.departmentId(), form.requesterId());
        if (confirmDocument.isNotDocumentOwner(raiseRequester)) {
            throw new ConfirmDocumentException(FAIL_SELF_VERIFICATION, form.requesterId());
        }

        if (confirmDocument.raiseImpossible()) {
            throw new ConfirmDocumentException(FAIL_RAISE + " 사유 : " + confirmDocument.getConfirmStatus().getDescription(), confirmDocument.getRequesterId());
        }

        confirmDocument.changeConfirmStatus(RAISE);
        return new ConfirmServiceResponse(confirmDocumentPk, form.requesterId());
    }

    private static ConfirmDocumentServiceResponse toConfirmServiceDto(ConfirmDocument confirmDocument) {
        return new ConfirmDocumentServiceResponse(
                confirmDocument.getPk(),
                confirmDocument.getDocument().getConfirmDocumentId(),
                confirmDocument.getRequester().getCompanyId(),
                confirmDocument.getRequester().getDepartmentId(),
                confirmDocument.getCreateSystem(),
                confirmDocument.getConfirmStatus(),
                confirmDocument.getDocument().getDocumentType(),
                confirmDocument.getRequester().getRequesterId());
    }

    public List<ConfirmDocumentServiceResponse> search(ConfirmDocumentSearchCondition condition) {
        return confirmDocumentMapper.search(condition);
    }

    public void acceptDocument(Long confirmDocumentPk, String approvalId) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(confirmDocumentPk)
                .orElseThrow(() -> new IllegalArgumentException());

        if (!RAISE.equals(confirmDocument.getConfirmStatus())) {
            throw new IllegalArgumentException("처리할 수 없습니다. 사유 :" + confirmDocument.getConfirmStatus());
        }

        List<ApprovalLine> approvalLines = confirmDocument.getApprovalLines();
        ApprovalLine findApprovalLine = approvalLines.stream()
                .filter(approvalLine -> approvalLine.matchApprovalLineId(approvalId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException());

        findApprovalLine.tryAccept();

    }

    @Transactional
    public ConfirmDocumentServiceResponse updateConfirmDocument(Long confirmDocumentPk, ConfirmDocumentUpdateForm form) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(confirmDocumentPk)
                .orElseThrow(() -> new IllegalArgumentException());

        return null;
    }

    @Transactional
    public void makeContent(Long confirmDocumentPk, List<ConfirmDocumentContentRequest> forms) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(confirmDocumentPk)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다."));
        Map<String, Object> confirmDocumentContents = new HashMap<>();

        for (ConfirmDocumentContentRequest form : forms) {
            confirmDocumentContents.put(form.elementKey(),
                    new ConfirmDocumentContentNameAndValue(form.elementName(), form.elementValue()));
        }
        ConfirmDocumentContent content = new ConfirmDocumentContent(confirmDocumentContents);
        confirmDocument.setContent(content);
        contentRepository.save(content);
    }

    @Transactional
    public ConfirmDocumentServiceResponse cancelConfirmDocument(Long confirmDocumentPk, ConfirmDocumentCancelForm form) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(confirmDocumentPk)
                .orElseThrow();

        // 취소 가능한 자인지
        Requester cancelRequester = new Requester(form.companyId(), form.departmentId(), form.requesterId());
        if (confirmDocument.isNotDocumentOwner(cancelRequester)) {
            throw new ConfirmDocumentException(FAIL_SELF_VERIFICATION, form.requesterId());
        }

        // 취소 가능한 상태인지
        if (confirmDocument.cancelImpossible()) {
            throw new ConfirmDocumentException(FAIL_CANCEL + " 사유 : " + confirmDocument.getConfirmStatus().getDescription(),confirmDocument.getRequesterId());
        }
        // 결재 문서 상태 변경
        confirmDocument.changeConfirmStatus(CANCEL);
        return new ConfirmDocumentServiceResponse(
                confirmDocument.getPk(),
                confirmDocument.getConfirmDocumentId(),
                confirmDocument.getCompanyId(),
                confirmDocument.getDepartmentId(),
                confirmDocument.getCreateSystem(),
                confirmDocument.getConfirmStatus(),
                confirmDocument.getDocumentType(),
                confirmDocument.getRequesterId());
    }
}
