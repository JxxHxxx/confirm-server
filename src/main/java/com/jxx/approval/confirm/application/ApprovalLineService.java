package com.jxx.approval.confirm.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.approval.common.client.SimpleRestClient;
import com.jxx.approval.confirm.application.server.dto.VerifyCompanyMemberDto;
import com.jxx.approval.confirm.application.server.function.VerifyCompanyMemberApi;
import com.jxx.approval.confirm.domain.*;
import com.jxx.approval.confirm.dto.request.ApprovalInformationForm;
import com.jxx.approval.confirm.dto.response.*;
import com.jxx.approval.confirm.infra.ConfirmDocumentRepository;
import com.jxx.approval.confirm.dto.request.ApprovalLineEnrollForm;
import com.jxx.approval.confirm.infra.ApprovalLineRepository;
import com.jxx.approval.vendor.vacation.VacationIdExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

import static com.jxx.approval.confirm.domain.ApprovalLineLifecycle.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalLineService {

    private final ApprovalLineRepository approvalLineRepository;
    private final ConfirmDocumentRepository confirmDocumentRepository;

    @Transactional
    public ApprovalLineResponse enrollApprovalLines(List<ApprovalLineEnrollForm> enrollForms, String confirmDocumentId) throws JsonProcessingException {
        Consumer<VerifyCompanyMemberDto> verifyCompanyMemberApi = new VerifyCompanyMemberApi();
        return enrollApprovalLines(enrollForms, confirmDocumentId, verifyCompanyMemberApi);
    }

    protected ApprovalLineResponse enrollApprovalLines(List<ApprovalLineEnrollForm> enrollForms, String confirmDocumentId, Consumer verifyCompanyMembersApi) throws JsonProcessingException {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByConfirmDocumentId(confirmDocumentId)
                .orElseThrow(() -> new IllegalArgumentException("결재 문서 ID " + confirmDocumentId + "가 존재하지 않습니다"));

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

        return createEnrollApprovalLinesResponse(confirmDocument, savedApprovalLines);
    }

    private static ApprovalLineResponse createEnrollApprovalLinesResponse(ConfirmDocument confirmDocument, List<ApprovalLine> savedApprovalLines) {
        List<ApprovalLineServiceDto> approvalLineServiceDtos = savedApprovalLines.stream()
                .map(ap -> new ApprovalLineServiceDto(
                        ap.getPk(),
                        ap.getApprovalOrder(),
                        ap.getApprovalLineId(),
                        ap.getApproveStatus()))
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
    @Transactional
    public void deleteApprovalLines(String confirmDocumentId) {
        ConfirmDocument confirmDocument = confirmDocumentRepository.findByConfirmDocumentId(confirmDocumentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 결재 문서를 찾을 수 없습니다."));

        if (confirmDocument.isNotRaiseBefore()) {
            throw new ConfirmDocumentException("해당 결재 문서의 결재선을 결재선을 삭제할 수 없습니다. 결재 문서 상태:" + confirmDocument.getConfirmStatus()
                    , confirmDocument.getRequesterId());
        }

        List<ApprovalLine> approvalLines = approvalLineRepository.findByConfirmDocumentId(confirmDocumentId);
        approvalLineRepository.deleteAll(approvalLines);

    }

    @Transactional
    public ApprovalLineServiceResponse accept(String confirmDocumentId, ApprovalInformationForm form) {
        // 리스너로 ConfirmDocument Status 체킹 해야됨
        List<ApprovalLine> approvalLines = approvalLineRepository.findByConfirmDocumentConfirmDocumentId(confirmDocumentId);

        ApprovalLineManager approvalLineManager = ApprovalLineManager.builder()
                .approvalLineId(form.approvalLineId())
                .approvalLines(approvalLines)
                .build()
                .isEmptyApprovalLine();

        ApprovalLine approvalLine = approvalLineManager
                .checkBelongInApprovalLine()
                .checkApprovalLineOrder()
                .changeApproveStatus(ApproveStatus.ACCEPT);

        boolean finalApproval = approvalLine.isFinalApproval(approvalLines);

        //여기 리팩토링 할 수 있음 일단 GO
        ConfirmDocument findDocument = confirmDocumentRepository.findByConfirmDocumentId(confirmDocumentId).orElseThrow();
        Long vacationId = VacationIdExtractor.execute(findDocument);

        return new ApprovalLineServiceResponse(
                approvalLine.getPk(),
                approvalLine.getApprovalOrder(),
                approvalLine.getApprovalLineId(),
                approvalLine.getApproveStatus(),
                finalApproval,
                vacationId);
    }

    @Transactional
    public ApprovalLineServiceResponse reject(String confirmDocumentId, ApprovalInformationForm form) {
        // 리스너로 ConfirmDocument Status 체킹 해야됨

        // 파기된 문서인지 체크
        List<ApprovalLine> approvalLines = approvalLineRepository.findByConfirmDocumentConfirmDocumentId(confirmDocumentId);

        ApprovalLineManager approvalLineManager = ApprovalLineManager.builder()
                .approvalLineId(form.approvalLineId())
                .approvalLines(approvalLines)
                .build();

        approvalLineManager.isEmptyApprovalLine();

        // 자신이 이미 결정한 사안인지 체크
        ApprovalLine approvalLine = approvalLineManager
                .checkBelongInApprovalLine()
                .checkApprovalLineOrder()
                .changeApproveStatus(ApproveStatus.REJECT);

        ConfirmDocument findDocument = confirmDocumentRepository.findByConfirmDocumentId(confirmDocumentId).orElseThrow();
        Long vacationId = VacationIdExtractor.execute(findDocument);

        boolean finalApproval = approvalLine.isFinalApproval(approvalLines);

        return new ApprovalLineServiceResponse(
                approvalLine.getPk(),
                approvalLine.getApprovalOrder(),
                approvalLine.getApprovalLineId(),
                approvalLine.getApproveStatus(),
                finalApproval,
                vacationId);
    }


    public List<ApprovalLineServiceDto> findByConfirmDocumentId(String confirmDocumentId) {
        List<ApprovalLine> approvalLines = approvalLineRepository.findByConfirmDocumentConfirmDocumentId(confirmDocumentId);

        return approvalLines.stream()
                .map(ap -> new ApprovalLineServiceDto(ap.getPk(),ap.getApprovalOrder(), ap.getApprovalLineId(), ap.getApproveStatus()))
                .toList();
    }
}
