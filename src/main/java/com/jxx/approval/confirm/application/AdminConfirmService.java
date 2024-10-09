package com.jxx.approval.confirm.application;

import com.jxx.approval.confirm.domain.connect.ConnectionElement;
import com.jxx.approval.confirm.domain.connect.RestApiConnection;
import com.jxx.approval.confirm.domain.line.ApprovalLine;
import com.jxx.approval.confirm.dto.request.ConnectionElementCreateRequest;
import com.jxx.approval.confirm.dto.request.RestApiConnectionCreateRequest;
import com.jxx.approval.confirm.dto.response.ApprovalLineServiceDto;
import com.jxx.approval.confirm.infra.ApprovalLineRepository;
import com.jxx.approval.confirm.infra.ConnectionElementRepository;
import com.jxx.approval.confirm.infra.RestApiConnectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminConfirmService {
    private final ApprovalLineRepository approvalLineRepository;

    private final RestApiConnectionRepository restApiConnectionRepository;
    private final ConnectionElementRepository connectionElementRepository;

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
}
