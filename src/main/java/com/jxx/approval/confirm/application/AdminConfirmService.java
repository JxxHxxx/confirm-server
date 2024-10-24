package com.jxx.approval.confirm.application;

import com.jxx.approval.common.page.PageService;
import com.jxx.approval.confirm.domain.AdminClientException;
import com.jxx.approval.confirm.domain.connect.ConnectionElement;
import com.jxx.approval.confirm.domain.connect.RestApiConnection;
import com.jxx.approval.confirm.domain.document.DocumentType;
import com.jxx.approval.confirm.domain.line.ApprovalLine;
import com.jxx.approval.confirm.dto.request.ConfirmConnectionApiRequest;
import com.jxx.approval.confirm.dto.request.RestApiConnectionCreateRequest;
import com.jxx.approval.confirm.dto.request.RestApiConnectionSearchCond;
import com.jxx.approval.confirm.dto.response.ApprovalLineServiceDto;
import com.jxx.approval.confirm.dto.response.RestApiConnectionResponse;
import com.jxx.approval.confirm.infra.ApprovalLineRepository;
import com.jxx.approval.confirm.infra.ConnectionElementRepository;
import com.jxx.approval.confirm.infra.RestApiConnectionAdminMapper;
import com.jxx.approval.confirm.infra.RestApiConnectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminConfirmService {
    private final ApprovalLineRepository approvalLineRepository;
    private final RestApiConnectionRepository restApiConnectionRepository;
    private final ConnectionElementRepository connectionElementRepository;
    private final RestApiConnectionAdminMapper restApiConnectionAdminMapper;
    private final PlatformTransactionManager txManager;

    public List<ApprovalLineServiceDto> findApprovalLinesBy(String confirmDocumentId) {
        List<ApprovalLine> approvalLines = approvalLineRepository.fetchByConfirmDocumentId(confirmDocumentId);

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

    public void createRestApiConnection(RestApiConnectionCreateRequest request) {
        RestApiConnection restApiConnection = RestApiConnection.builder()
                .description(request.description())
                .documentType(request.documentType())
                .host(request.host())
                .methodType(request.methodType())
                .path(request.path())
                .scheme(request.scheme())
                .triggerType(request.triggerType())
                .build();
        restApiConnectionRepository.save(restApiConnection);

        List<ConnectionElement> connectionElements = request.connectionElements().stream()
                .map(r -> ConnectionElement.builder()
                        .elementType(r.elementType())
                        .elementKey(r.elementKey())
                        .elementValue(r.elementValue())
                        .elementValueType(r.elementValueType())
                        .build()
                ).toList();

        connectionElementRepository.saveAll(connectionElements);
    }

    // WRITE QUERY - 트랜잭션 직접 선언
    public RestApiConnectionResponse mappingConfirmApi(ConfirmConnectionApiRequest request) {
        //  TRIGGER_TYPE, DOCUMENT_TYPE 두개는 고유한 값을 가져야 함
        validateUnique(request);
        // API 가 정상 호출 여부는 스케줄러를 통해 확인
        // 약식에 맞아야함
        // HOST, METHOD_TYPE, PATH, PORT, SCHEME 이용해서 호출
        validateMappingApiParams(request);

        // 트랜잭션 시작
        TransactionStatus txStatus = txManager.getTransaction(TransactionDefinition.withDefaults());
        RestApiConnection restApiConnection = restApiConnectionRepository.save(RestApiConnection.builder()
                .methodType(request.methodType())
                .scheme(request.scheme())
                .path(request.path())
                .triggerType(request.triggerType())
                .description(request.description())
                .host(request.host())
                .port(request.port())
                .requesterId(request.requesterId())
                .creteDateTime(LocalDateTime.now())
                .used(true)
                .build());
        txManager.commit(txStatus); // 트랜잭션 종료

        return new RestApiConnectionResponse(
                restApiConnection.getConnectionPk(),
                restApiConnection.getDescription(),
                restApiConnection.getScheme(),
                restApiConnection.getHost(),
                restApiConnection.getPort(),
                restApiConnection.getMethodType(),
                restApiConnection.getPath(),
                restApiConnection.getTriggerType(),
                restApiConnection.getDocumentType(),
                restApiConnection.getCreateDateTime(),
                restApiConnection.getRequesterId(),
                restApiConnection.isUsed());
    }

    private void validateUnique(ConfirmConnectionApiRequest request) {
        DocumentType documentType = request.documentType();
        String triggerType = request.triggerType();
        boolean present = restApiConnectionRepository.findByDocumentTypeAndTriggerType(documentType, triggerType)
                .isPresent();

        if (present) {
            throw new AdminClientException("documentType:" + documentType + "triggerType:" + triggerType + "is present");
        }
    }

    private void validateMappingApiParams(ConfirmConnectionApiRequest request) {
        boolean validatePath = RestApiConnection.validatePath(request.path());
        boolean validateScheme = RestApiConnection.validateScheme(request.scheme());
        boolean validatePort = RestApiConnection.validatePort(request.port());
        boolean validateMethodType = RestApiConnection.validateMethodType(request.methodType());

        if (!(validatePath && validateScheme && validatePort && validateMethodType)) {
            log.error("validatePath : {} validateScheme : {} validatePort : {} validateMethodType : {}",
                    validatePath, validateScheme, validatePort, validateMethodType);
            throw new AdminClientException("유효하지 않은 연동 API 정보가 존재합니다.");
        }
    }

    public Page<RestApiConnectionResponse> searchMappingConfirmApi(RestApiConnectionSearchCond cond, int size, int page) {
        List<RestApiConnectionResponse> responses = restApiConnectionAdminMapper.search(cond);
        PageService pageService = new PageService(page, size);
        return pageService.convertToPage(responses);
    }
}
