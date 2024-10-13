package com.jxx.approval.confirm.application;

import com.jxx.approval.confirm.domain.document.*;
import com.jxx.approval.confirm.domain.line.ApprovalLineException;
import com.jxx.approval.confirm.domain.line.ApprovalLineLifecycle;
import com.jxx.approval.confirm.domain.service.ConfirmDocumentIdGenerator;
import com.jxx.approval.confirm.dto.request.*;
import com.jxx.approval.confirm.dto.response.*;
import com.jxx.approval.confirm.infra.ConfirmDocumentContentRepository;
import com.jxx.approval.confirm.infra.ConfirmDocumentMapper;
import com.jxx.approval.confirm.infra.ConfirmDocumentRepository;
import com.jxx.approval.confirm.listener.ConfirmDocumentRaiseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;

import static com.jxx.approval.confirm.domain.line.ApprovalLineException.EMPTY_APPROVAL_LINE;
import static com.jxx.approval.confirm.domain.document.ConfirmStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmDocumentService {

    private final ConfirmDocumentRepository confirmDocumentRepository;
    private final ConfirmDocumentMapper confirmDocumentMapper;
    private final ConfirmDocumentContentRepository contentRepository;
    private final ApplicationEventPublisher eventPublisher;

    /*여기서 Content 까지 같이 만들어야 함 */
    @Transactional
    public void createConfirmDocument(ConfirmCreateForm form) {
        Document document = new Document(form.documentType());
        Requester requester = new Requester(
                form.companyId(),
                form.departmentId(),
                form.departmentName(),
                form.requesterId(),
                form.requesterName());

        ConfirmDocument confirmDocument = ConfirmDocument.builder()
                .confirmStatus(CREATE)
                .approvalLineLifecycle(ApprovalLineLifecycle.BEFORE_CREATE)
                .confirmDocumentId(ConfirmDocumentIdGenerator.generate(form))
                .document(document)
                .requester(requester)
                .createSystem(form.createSystem())
                .build();

        ConfirmDocument savedConfirmDocument = confirmDocumentRepository.save(confirmDocument);

        // case null contents defense
        Map<String, Object> documentContents = Objects.isNull(form.contents()) ? new HashMap<>() : form.contents();
        ConfirmDocumentContent confirmDocumentContent = new ConfirmDocumentContent(documentContents);

        savedConfirmDocument.setContent(confirmDocumentContent);

        contentRepository.save(confirmDocumentContent);
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
        Requester raiseRequester = new Requester(form.companyId(), form.departmentId(),
                null, form.requesterId(), null);
        if (confirmDocument.isNotDocumentOwner(raiseRequester)) {
            throw new ConfirmDocumentException(ConfirmDocumentException.FAIL_SELF_VERIFICATION, form.requesterId());
        }
        if (confirmDocument.raiseImpossible()) {
            // 결재 문서의 상태를 던짐, exceptionHandler 의 data 필드에 매핑
            throw new ConfirmDocumentException("해당 결재 문서를 상신할 수 없는 상태입니다.",
                    confirmDocument.getRequesterId(),
                    confirmDocument.getConfirmStatus().name());
        }
        // 결재선 지정 여부
        if (!confirmDocument.approvalLineCreated()) {
            throw new ApprovalLineException(EMPTY_APPROVAL_LINE);
        }
        // WRITE QUERY
        confirmDocument.changeConfirmStatus(RAISE);
        confirmDocument.changeApprovalLineCycle(ApprovalLineLifecycle.PROCESS_MODIFIABLE);

        eventPublisher.publishEvent(new ConfirmDocumentRaiseEvent(confirmDocument, "RAISE"));
        return new ConfirmDocumentServiceDto(confirmDocumentId, form.requesterId(), confirmDocument.getConfirmStatus());
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
                confirmDocument.getCompletedTime(),
                confirmDocument.getDocument().getDocumentType(),
                confirmDocument.getRequester().getRequesterId(),
                confirmDocument.getRequester().getRequesterName(),
                confirmDocument.receiveContentPk(),
                confirmDocument.getApprovalLineLifecycle(),
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
    public ConfirmDocumentServiceDto cancelConfirmDocument(String confirmDocumentId, ConfirmDocumentCancelForm form) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findWithContent(confirmDocumentId)
                .orElseThrow();

        // 취소 가능한 자인지
        Requester cancelRequester = new Requester(form.companyId(), form.departmentId(),
                null, form.requesterId(), null);
        if (confirmDocument.isNotDocumentOwner(cancelRequester)) {
            throw new ConfirmDocumentException(ConfirmDocumentException.FAIL_SELF_VERIFICATION, form.requesterId());
        }

        // 취소 가능한 상태인지
        ConfirmStatus confirmStatus = confirmDocument.getConfirmStatus();
        if (confirmDocument.cancelImpossible()) {
            // 결재 문서가 취소라면 CD03, 그 외 취소 불가능한 상태라면 CD04
            String errCode = CANCEL.equals(confirmStatus) ? "CD03" : "CD04";
            throw new ConfirmDocumentException(
                    "CREATE, UPDATE 상태의 결재 문서만 삭제 가능합니다. 현재 결재 문서 상태 : " + confirmDocument.getConfirmStatus(),
                    confirmDocument.getRequesterId(),
                    errCode);
        }
        // 결재 문서 상태 변경
        confirmDocument.changeConfirmStatus(CANCEL);
        return new ConfirmDocumentServiceDto(confirmDocumentId, form.requesterId(), confirmDocument.getConfirmStatus());
    }


    // 결재 문서, 결재선 함께 조회
    public List<ConfirmDocumentWithApprovalLineResponse> fetchWithApprovalLines(ConfirmDocumentSearchCondition condition) {
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

    public List<ConfirmDocumentServiceResponse> findSelfDraftConfirmDocuments(String companyId, String departmentId, String memberId) {
        List<ConfirmDocument> confirmDocuments = confirmDocumentRepository.findSelfDraftWithContentAfterRaise(companyId, departmentId, memberId);
        return confirmDocuments.stream()
                .map(ConfirmDocumentService::toConfirmDocumentServiceResponse)
                .toList();
    }

    public List<ConfirmDocumentServiceResponse> findDepartmentConfirmDocument(String companyId, String departmentId) {
        List<ConfirmDocument> confirmDocuments = confirmDocumentRepository.findWithContentAfterRaise(companyId, departmentId);

        return confirmDocuments.stream()
                .map(ConfirmDocumentService::toConfirmDocumentServiceResponse)
                .toList();
    }

    public List<ConfirmDocumentServiceResponse> searchDocuments(ConfirmDocumentSearchCondition condition) {
        List<ConfirmDocumentServiceResponse> VanillaResponses = confirmDocumentMapper.search(condition);
        List<ConfirmDocumentServiceResponse> filteredResponses = filterDuplication(VanillaResponses);
        return filteredResponses;
    }

    public List<ConfirmDocumentServiceResponse> findByConfirmDocumentId(String confirmDocumentId) {
        List<ConfirmDocumentServiceResponse> VanillaResponses = confirmDocumentMapper.findByConfirmDocumentId(confirmDocumentId);
        List<ConfirmDocumentServiceResponse> filteredResponses = filterDuplication(VanillaResponses);
        return filteredResponses;
    }

    private static List<ConfirmDocumentServiceResponse> filterDuplication(List<ConfirmDocumentServiceResponse> VanillaResponses) {
        List<ConfirmDocumentServiceResponse> filteredResponses = new ArrayList<>();
        for (ConfirmDocumentServiceResponse vResponse : VanillaResponses) {
            // 응답에 중복된 결재 문서가 존재할 수 있다. 결재 문서:결재 선 = 1:N 구조라서 JOIN해서 가져올 때 여러개를 가져오게 됨
            boolean duplicated = filteredResponses.stream()
                    .anyMatch(fr -> Objects.equals(vResponse.getConfirmDocumentId(), fr.getConfirmDocumentId()));
            // 중복 데이터가 아니면 넣는다.
            if (!duplicated) {
                filteredResponses.add(vResponse);
            }
        }
        return filteredResponses;
    }
}
