package com.jxx.approval.confirm.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.approval.confirm.application.server.dto.VerifyCompanyMemberDto;
import com.jxx.approval.confirm.application.server.function.VerifyCompanyMemberApi;
import com.jxx.approval.confirm.domain.document.ConfirmDocument;
import com.jxx.approval.confirm.domain.document.ConfirmDocumentException;
import com.jxx.approval.confirm.domain.document.ConfirmStatus;
import com.jxx.approval.confirm.domain.document.Requester;
import com.jxx.approval.confirm.domain.line.ApprovalLine;
import com.jxx.approval.confirm.domain.line.ApprovalLineManager;
import com.jxx.approval.confirm.domain.line.ApproveStatus;
import com.jxx.approval.confirm.dto.request.ApprovalInformationForm;
import com.jxx.approval.confirm.dto.response.*;
import com.jxx.approval.confirm.infra.ConfirmDocumentRepository;
import com.jxx.approval.confirm.dto.request.ApprovalLineEnrollForm;
import com.jxx.approval.confirm.infra.ApprovalLineRepository;
import com.jxx.approval.confirm.listener.ConfirmDocumentAcceptRejectEvent;
import com.jxx.approval.confirm.listener.ConfirmDocumentFinalAcceptDecisionEvent;
import com.jxx.approval.confirm.listener.ConfirmDocumentRejectDecisionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

import static com.jxx.approval.confirm.domain.line.ApprovalLineLifecycle.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalLineService {

    private final ApprovalLineRepository approvalLineRepository;
    private final ConfirmDocumentRepository confirmDocumentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ApprovalLineResponse enrollApprovalLines(List<ApprovalLineEnrollForm> enrollForms, String confirmDocumentId) throws JsonProcessingException {
        Consumer<VerifyCompanyMemberDto> verifyCompanyMemberApi = new VerifyCompanyMemberApi();
        return enrollApprovalLines(enrollForms, confirmDocumentId, verifyCompanyMemberApi);
    }

    /* CONTENTS 없으면 DB에서 못가져옴*/
    protected ApprovalLineResponse enrollApprovalLines(List<ApprovalLineEnrollForm> enrollForms, String confirmDocumentId, Consumer verifyCompanyMembersApi) throws JsonProcessingException {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findWithContent(confirmDocumentId)
                .orElseThrow(() -> new IllegalArgumentException("결재 문서 ID " + confirmDocumentId + "를 가져올 수 없습니다."));

        // 상신 전 상태인지 확인
        if (confirmDocument.isNotRaiseBefore()) {
            throw new ConfirmDocumentException("해당 결재 문서는 결재선을 수정할 수 없는 상태입니다." + confirmDocument.getConfirmStatus()
                    , confirmDocument.getRequesterId());
        }
        // 이미 결재선이 등록되어 있는지 확인
        if (!BEFORE_CREATE.equals(confirmDocument.getApprovalLineLifecycle())) {
            throw new ConfirmDocumentException("이미 결재선이 지정된 결재 문서입니다.", confirmDocument.getRequesterId(), "AP01");
        }

        // 결재선에 지정된 사용자가 사내 구성원인지 검증
        List<String> approvalMembersId = enrollForms.stream().map(ApprovalLineEnrollForm::approvalId).toList();
        VerifyCompanyMemberDto companyMemberDto = new VerifyCompanyMemberDto(confirmDocument.getCompanyId(), approvalMembersId);
        verifyCompanyMembersApi.accept(companyMemberDto);

        List<ApprovalLine> requestApprovalLines = enrollForms.stream()
                .map(form -> new ApprovalLine(
                        form.approvalOrder(),
                        form.approvalId(),
                        form.approvalName(),
                        form.approvalDepartmentId(),
                        form.approvalDepartmentName(),
                        confirmDocument))
                .toList();
        List<ApprovalLine> savedApprovalLines = approvalLineRepository.saveAll(requestApprovalLines);

        confirmDocument.changeApprovalLineCycle(CREATED);

        return approvalLineResponse(confirmDocument, savedApprovalLines);
    }

    private static ApprovalLineResponse approvalLineResponse(ConfirmDocument confirmDocument, List<ApprovalLine> savedApprovalLines) {
        List<ApprovalLineServiceDto> approvalLineServiceDtos = savedApprovalLines.stream()
                .map(ap -> new ApprovalLineServiceDto(
                        ap.getPk(),
                        ap.getApprovalOrder(),
                        ap.getApprovalLineId(),
                        ap.getApproveStatus(),
                        ap.getApprovalName(),
                        ap.getApproveTime()))
                .toList();
        return new ApprovalLineResponse(
                new ConfirmDocumentServiceDto(
                        confirmDocument.getConfirmDocumentId(),
                        confirmDocument.getRequesterId(),
                        confirmDocument.getConfirmStatus()
                ),
                approvalLineServiceDtos);
    }

    // 제거 -> 추가는 다시 ENROLL API 사용하도록
    // 이미 상신된 문서는 결재선 변경 불가능 -> 상신된 문서 결재선을 변경하기 위해서는
    // 상신된 문서의 상신을 취소하고 -> 결재선 변경을 하도록 해야 한다.
    @Transactional
    public ApprovalLineResponse deleteApprovalLines(String confirmDocumentId, String memberId) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findWithContent(confirmDocumentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 결재 문서를 찾을 수 없습니다."));

        // 인가 로직 start
        Requester requester = confirmDocument.getRequester();
        if (requester.isNotRequester(memberId)) {
            throw new ConfirmDocumentException("삭제 권한을 가지고 있지 않습니다.");
        };
        // 인가 로직 end
        if (confirmDocument.isNotRaiseBefore()) {
            throw new ConfirmDocumentException("해당 결재 문서의 결재선을 결재선을 삭제할 수 없습니다. 결재 문서 상태:" + confirmDocument.getConfirmStatus()
                    , confirmDocument.getRequesterId());
        }


        // 추후 history 성 데이터도 넣을 것
        List<ApprovalLine> approvalLines = approvalLineRepository.findByConfirmDocumentId(confirmDocumentId);
        approvalLineRepository.deleteAll(approvalLines);

        return approvalLineResponse(confirmDocument, approvalLines);
    }

    @Transactional
    public ApprovalLineServiceResponse accept(String confirmDocumentId, ApprovalInformationForm form) {
        List<ApprovalLine> approvalLines = approvalLineRepository.fetchByConfirmDocumentId(confirmDocumentId);

        ApprovalLineManager approvalLineManager = ApprovalLineManager.builder()
                .approvalLineId(form.approvalLineId())
                .approvalLines(approvalLines)
                .build()
                .isEmptyApprovalLine(); // 빈 결재 라인인지 검증

        //빈 결재 라인 검증을 위에서 하기 때문에 IndexOutOfBoundsException 예외는 뜨지 않음
        ConfirmDocument confirmDocument = approvalLines.get(0).getConfirmDocument();

        // ConfirmDocument 에서 결재자가 결재 문서를 승인할 수 있는 상태인지 검증
        if (confirmDocument.isNotChangeTo(ConfirmStatus.ACCEPT)) {
            throw new ConfirmDocumentException("현재 결재 문서는 발려 할 수 없는 상태입니다.");
        }

        // dirty checking
        ApprovalLine approvalLine = approvalLineManager
                .checkBelongInApprovalLine()
                .checkApprovalLineOrder()
                .changeApproveStatus(ApproveStatus.ACCEPT);
        // 최종 결정권자 여부 - 메모리 상에서만 확인 가능, 다시 말해 DB에 없는 필드

        // 최종 승인자가 승인을 했을 때 발생하는 이벤트로 일반적으로 타 서버의 API 를 호출한다.
        // 다만 changeApproveStatus(ApproveStatus.ACCEPT) 에서 발생하는 UPDATE 쿼리와 트랜잭션을 분리하게되면 데이터 정합성이 맞지 않을 수 있어 트랜잭션을 유지한다.
        if (approvalLine.isFinalApproval(approvalLines)) {
            if (confirmDocument.anyApprovalNotAccepted()) {
                log.error("confirmDocumentId:{} present reject flag, so cannot process accept logic", confirmDocumentId);
                throw new ConfirmDocumentException("잘못된 접근입니다. 관리자에게 문의하세요.");
            }

            // 결재 문서 상태 변경 및 최종 승인/반려 시간 지정
            // WRITE QUERY : JPA dirty checking
            confirmDocument.processFinalDecisionConfirmDocument(ConfirmStatus.ACCEPT);
            eventPublisher.publishEvent(new ConfirmDocumentFinalAcceptDecisionEvent(confirmDocument));
        }

        return new ApprovalLineServiceResponse(
                approvalLine.getPk(),
                approvalLine.getApprovalOrder(),
                approvalLine.getApprovalLineId(),
                approvalLine.getApproveStatus(),
                approvalLine.isFinalApproval(approvalLines),
                confirmDocument.getDocumentType(),
                confirmDocument.getCompanyId(),
                confirmDocumentId);
    }

    @Transactional
    public ApprovalLineServiceResponse reject(String confirmDocumentId, ApprovalInformationForm form) {
        // 리스너로 ConfirmDocument Status 체킹 해야됨

        // 파기된 문서인지 체크
        List<ApprovalLine> approvalLines = approvalLineRepository.fetchByConfirmDocumentId(confirmDocumentId);

        ApprovalLineManager approvalLineManager = ApprovalLineManager.builder()
                .approvalLineId(form.approvalLineId())
                .approvalLines(approvalLines)
                .build()
                .isEmptyApprovalLine();

        //추가 로직 - 결재 문서 타입을 넘기기 위함
        ConfirmDocument confirmDocument = approvalLines.get(0).getConfirmDocument();

        if (confirmDocument.isNotChangeTo(ConfirmStatus.REJECT)) {
            throw new ConfirmDocumentException("현재 결재 문서는 발려 할 수 없는 상태입니다.");
        }

        eventPublisher.publishEvent(ConfirmDocumentAcceptRejectEvent.rejectEvent(confirmDocument));

        // 자신이 이미 결정한 사안인지 체크
        // WRITE QUERY
        ApprovalLine approvalLine = approvalLineManager
                .checkBelongInApprovalLine()
                .checkApprovalLineOrder()
                .changeApproveStatus(ApproveStatus.REJECT);
        confirmDocument.processFinalDecisionConfirmDocument(ConfirmStatus.REJECT);

        //반려의 경우, 중간 결재자가 반려하면 이전, 이후 결재자의 승인 여부와 상관없이 반려에 대한 후속 처리를 해야함
        eventPublisher.publishEvent(new ConfirmDocumentRejectDecisionEvent(confirmDocument));

        boolean finalApproval = approvalLine.isFinalApproval(approvalLines);

        return new ApprovalLineServiceResponse(
                approvalLine.getPk(),
                approvalLine.getApprovalOrder(),
                approvalLine.getApprovalLineId(),
                approvalLine.getApproveStatus(),
                finalApproval,
                confirmDocument.getDocumentType(),
                confirmDocument.getCompanyId(),
                confirmDocumentId);
    }

    // TODO 다른 회사의 결재 라인을 볼 수 있는 문제 해결해야함
    public List<ApprovalLineServiceDto> findByConfirmDocumentId(String confirmDocumentId, String companyId) {
        List<ApprovalLine> approvalLines = approvalLineRepository.fetchByConfirmDocumentId(confirmDocumentId);
        boolean isNotOurCompanyDocument = approvalLines.stream()
                .anyMatch(ap -> {
                    ConfirmDocument confirmDocument = ap.getConfirmDocument();
                    return !confirmDocument.areOurCompanyConfirmDocument(companyId);
                });
        if (isNotOurCompanyDocument) {
            // logging 작업 해야함
            throw new ConfirmDocumentException("접근 권한이 없습니다.");
        }

        return approvalLines.stream()
                .map(ap -> new ApprovalLineServiceDto(
                        ap.getPk(),
                        ap.getApprovalOrder(),
                        ap.getApprovalLineId(),
                        ap.getApproveStatus(),
                        ap.getApprovalName(),
                        ap.getApproveTime()))
                .toList();
    }
}
