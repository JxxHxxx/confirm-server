package com.jxx.approval.confirm.application;

import com.jxx.approval.confirm.domain.*;
import com.jxx.approval.confirm.dto.request.*;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentAndContentServiceResponse;
import com.jxx.approval.confirm.dto.response.ConfirmServiceResponse;
import com.jxx.approval.confirm.infra.ApprovalLineRepository;
import com.jxx.approval.confirm.infra.ConfirmDocumentContentRepository;
import com.jxx.approval.confirm.infra.ConfirmDocumentMapper;
import com.jxx.approval.confirm.infra.ConfirmDocumentRepository;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConfirmDocumentService {

    private final ConfirmDocumentRepository confirmDocumentRepository;
    private final ConfirmDocumentMapper confirmDocumentMapper;
    private final ConfirmDocumentContentRepository contentRepository;
    private final ApprovalLineRepository approvalLineRepository;

    @Transactional
    public void createConfirmDocument(ConfirmCreateForm form) {
        Document document = new Document(form.documentType());
        Requester requester = new Requester(form.companyId(), form.departmentId(), form.requesterId());

        ConfirmDocument confirmDocument = ConfirmDocument.builder()
                .document(document)
                .requester(requester)
                .createSystem(form.createSystem())
                .approvalLineStatus(ApprovalLineStatus.BEFORE_CREATE)
                .build();

        confirmDocumentMapper.save(confirmDocument);
    }

    public ConfirmDocumentServiceResponse readByPk(Long confirmDocumentPk) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(confirmDocumentPk)
                .orElseThrow(() -> new IllegalArgumentException("없어요~"));

        return toConfirmDocumentServiceResponse(confirmDocument);
    }

    public List<ConfirmDocumentServiceResponse> readAll() {
        List<ConfirmDocument> confirmDocuments = confirmDocumentRepository.findAll();

        return confirmDocuments.stream()
                .map(confirmDocument -> toConfirmDocumentServiceResponse(confirmDocument))
                .toList();
    }

    @Transactional
    public ConfirmServiceResponse raise(String confirmDocumentId, ConfirmRaiseForm form) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByDocumentConfirmDocumentId(confirmDocumentId)
                .orElseThrow(() -> new IllegalArgumentException());

        Requester raiseRequester = new Requester(form.companyId(), form.departmentId(), form.requesterId());
        if (confirmDocument.isNotDocumentOwner(raiseRequester)) {
            throw new ConfirmDocumentException(ConfirmDocumentException.FAIL_SELF_VERIFICATION, form.requesterId());
        }

        ConfirmStatus confirmStatus = confirmDocument.getConfirmStatus();
        if (confirmDocument.raiseImpossible()) {
            throw new ConfirmDocumentException(ConfirmDocumentException.FAIL_RAISE + " 사유 : " + confirmStatus.getDescription(), confirmDocument.getRequesterId());
        }

        ApprovalLineManager approvalLineManager = ApprovalLineManager.builder()
                .approvalLines(confirmDocument.getApprovalLines())
                .build();
        approvalLineManager.isEmptyApprovalLine();

        confirmDocument.changeConfirmStatus(ConfirmStatus.RAISE);
        return new ConfirmServiceResponse(confirmDocumentId, form.requesterId(), confirmStatus);
    }

    private static ConfirmDocumentServiceResponse toConfirmDocumentServiceResponse(ConfirmDocument confirmDocument) {
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

    @Transactional
    public ConfirmDocumentServiceResponse updateConfirmDocument(Long confirmDocumentPk, ConfirmDocumentUpdateForm form) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(confirmDocumentPk)
                .orElseThrow(() -> new IllegalArgumentException());

        return null;
    }

    @Transactional
    public ConfirmDocumentAndContentServiceResponse makeContent(Long confirmDocumentPk, List<ConfirmDocumentContentRequest> forms) {
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

        ConfirmDocumentServiceResponse confirmDocumentServiceResponse = new ConfirmDocumentServiceResponse(
                        confirmDocument.getPk(),
                        confirmDocument.getConfirmDocumentId(),
                        confirmDocument.getCompanyId(),
                        confirmDocument.getDepartmentId(),
                        confirmDocument.getCreateSystem(),
                        confirmDocument.getConfirmStatus(),
                        confirmDocument.getDocumentType(),
                        confirmDocument.getRequesterId());

        ConfirmDocumentContent confirmDocumentContent = confirmDocument.getContent();
        Map<String, Object> contents = confirmDocumentContent.getBody();

        return new ConfirmDocumentAndContentServiceResponse(confirmDocumentServiceResponse, contents);

    }

    @Transactional
    public ConfirmDocumentServiceResponse cancelConfirmDocument(Long confirmDocumentPk, ConfirmDocumentCancelForm form) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByPk(confirmDocumentPk)
                .orElseThrow();

        // 취소 가능한 자인지
        Requester cancelRequester = new Requester(form.companyId(), form.departmentId(), form.requesterId());
        if (confirmDocument.isNotDocumentOwner(cancelRequester)) {
            throw new ConfirmDocumentException(ConfirmDocumentException.FAIL_SELF_VERIFICATION, form.requesterId());
        }

        // 취소 가능한 상태인지
        if (confirmDocument.cancelImpossible()) {
            throw new ConfirmDocumentException(ConfirmDocumentException.FAIL_CANCEL + " 사유 : " + confirmDocument.getConfirmStatus().getDescription(),confirmDocument.getRequesterId());
        }
        // 결재 문서 상태 변경
        confirmDocument.changeConfirmStatus(ConfirmStatus.CANCEL);
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
