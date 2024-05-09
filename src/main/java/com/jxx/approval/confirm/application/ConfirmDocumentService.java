package com.jxx.approval.confirm.application;

import com.jxx.approval.confirm.domain.*;
import com.jxx.approval.confirm.dto.request.*;
import com.jxx.approval.confirm.dto.response.*;
import com.jxx.approval.confirm.infra.ConfirmDocumentContentRepository;
import com.jxx.approval.confirm.infra.ConfirmDocumentMapper;
import com.jxx.approval.confirm.infra.ConfirmDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.jxx.approval.confirm.domain.ApprovalLineException.EMPTY_APPROVAL_LINE;

@Service
@RequiredArgsConstructor
public class ConfirmDocumentService {

    private final ConfirmDocumentRepository confirmDocumentRepository;
    private final ConfirmDocumentMapper confirmDocumentMapper;
    private final ConfirmDocumentContentRepository contentRepository;

    @Transactional
    public void createConfirmDocument(ConfirmCreateForm form) {
        Document document = new Document(form.documentType());
        Requester requester = new Requester(form.companyId(), form.departmentId(), form.requesterId());

        ConfirmDocument confirmDocument = ConfirmDocument.builder()
                .document(document)
                .requester(requester)
                .createSystem(form.createSystem())
                .approvalLineLifecycle(ApprovalLineLifecycle.BEFORE_CREATE)
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
    public ConfirmDocumentServiceDto raise(String confirmDocumentId, ConfirmRaiseForm form) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findWithContent(confirmDocumentId)
                .orElseThrow(() -> new IllegalArgumentException());

        // 요청자 검증
        Requester raiseRequester = new Requester(form.companyId(), form.departmentId(), form.requesterId());
        if (confirmDocument.isNotDocumentOwner(raiseRequester)) {
            throw new ConfirmDocumentException(ConfirmDocumentException.FAIL_SELF_VERIFICATION, form.requesterId());
        }

        if (confirmDocument.raiseImpossible()) {
            // 결재 문서 상태 던져야함
            throw new ConfirmDocumentException("해당 결재 문서를 상신할 수 없는 상태입니다.", confirmDocument.getRequesterId());
        }
        // 결재선 지정 여부
        if (!confirmDocument.approvalLineCreated()) {
            throw new ApprovalLineException(EMPTY_APPROVAL_LINE);
        };

        confirmDocument.changeConfirmStatus(ConfirmStatus.RAISE);
        confirmDocument.changeApprovalLineCycle(ApprovalLineLifecycle.PROCESS_MODIFIABLE);

        ConfirmStatus updatedConfirmStatus = confirmDocument.getConfirmStatus();
        return new ConfirmDocumentServiceDto(confirmDocumentId, form.requesterId(), updatedConfirmStatus);
    }

    //
    public ConfirmDocumentServiceDto cancelRaise(String confirmDocumentId) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findWithContent(confirmDocumentId)
                .orElseThrow(() -> new IllegalArgumentException());

        // 이미 반려/승인자가 있을 경우 못함


        return null;
    }

    private static ConfirmDocumentServiceResponse toConfirmDocumentServiceResponse(ConfirmDocument confirmDocument) {
        return new ConfirmDocumentServiceResponse(
                confirmDocument.getPk(),
                confirmDocument.getConfirmDocumentId(),
                confirmDocument.getCreateTime(),
                confirmDocument.getRequester().getCompanyId(),
                confirmDocument.getRequester().getDepartmentId(),
                confirmDocument.getRequester().getDepartmentName(),
                confirmDocument.getCreateSystem(),
                confirmDocument.getConfirmStatus(),
                confirmDocument.getDocument().getDocumentType(),
                confirmDocument.getRequester().getRequesterId(),
                confirmDocument.getRequester().getRequesterName(),
                confirmDocument.receiveContentPk(),
                confirmDocument.receiveContents());
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
        ConfirmDocumentContent savedContent = contentRepository.save(content);

        ConfirmDocumentServiceResponse confirmDocumentServiceResponse = toConfirmDocumentServiceResponse(confirmDocument);

        ConfirmDocumentContent confirmDocumentContent = confirmDocument.getContent();
        Map<String, Object> contents = confirmDocumentContent.getContents();

        return new ConfirmDocumentAndContentServiceResponse(confirmDocumentServiceResponse, savedContent.getPk(), contents);
    }

    @Transactional
    public ConfirmDocumentServiceResponse cancelConfirmDocument(String confirmDocumentId, ConfirmDocumentCancelForm form) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findWithContent(confirmDocumentId)
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
        return toConfirmDocumentServiceResponse(confirmDocument);
    }


    public List<ConfirmDocumentWithApprovalLineResponse> fetchWithApprovalLines(ConfirmDocumentSearchConditionQueryString condition) {
        return confirmDocumentMapper.fetchWithApprovalLine(condition);
    }
    public ConfirmDocumentAndContentServiceResponse findDocumentContent(Long contentPk) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findWithContent(contentPk)
                .orElseThrow(() -> new IllegalArgumentException("contentPk:" + contentPk + " is not exist"));

        ConfirmDocumentServiceResponse confirmDocumentResponse = toConfirmDocumentServiceResponse(confirmDocument);

        ConfirmDocumentContent content = confirmDocument.getContent();

        if (Objects.isNull(content)) {
            return new ConfirmDocumentAndContentServiceResponse(
                    confirmDocumentResponse,
                    null,
                    Map.of());
        }

        return new ConfirmDocumentAndContentServiceResponse(
                confirmDocumentResponse,
                content.getPk(),
                content.getContents());
    }

    public List<ConfirmDocumentServiceResponse> findDepartmentConfirmDocument(String companyId, String departmentId) {
        List<ConfirmDocument> confirmDocuments = confirmDocumentRepository.findWithContent(companyId, departmentId);

        return confirmDocuments.stream()
                .map(ConfirmDocumentService::toConfirmDocumentServiceResponse)
                .toList();
    }
}
