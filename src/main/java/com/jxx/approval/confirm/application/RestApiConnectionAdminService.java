package com.jxx.approval.confirm.application;

import com.jxx.approval.common.page.PageService;
import com.jxx.approval.confirm.domain.connect.RestApiConnResponseCode;
import com.jxx.approval.confirm.domain.connect.RestApiConnection;
import com.jxx.approval.confirm.domain.connect.RestApiConnectionException;
import com.jxx.approval.confirm.domain.connect.RestApiConnectionValidator;
import com.jxx.approval.confirm.dto.request.RestApiConnectionSearchCond;
import com.jxx.approval.confirm.dto.response.RestApiConnectionResponse;
import com.jxx.approval.confirm.infra.RestApiConnectionAdminMapper;
import com.jxx.approval.confirm.infra.RestApiConnectionRepository;
import com.jxx.approval.confirm.domain.connect.dto.CreateMappingConfirmApiRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestApiConnectionAdminService {

    private final RestApiConnectionAdminMapper restApiConnectionAdminMapper;
    private final RestApiConnectionRepository restApiConnectionRepository;
    public Page<RestApiConnectionResponse> searchMappingConfirmApi(RestApiConnectionSearchCond cond, int size, int page) {
        List<RestApiConnectionResponse> responses = restApiConnectionAdminMapper.search(cond);
        PageService pageService = new PageService(page, size);
        return pageService.convertToPage(responses);
    }
    public RestApiConnectionResponse createMappingConfirmApi(CreateMappingConfirmApiRequest request) {
        // 결재 문서의 트리거가 존재하는지 검증 - 존재한다면 오류, 미존재 시, 생성 진행 가능
        boolean duplicatedRequest = restApiConnectionRepository.findByDocumentTypeAndTriggerType(request.documentType(), request.triggerType())
                .isPresent();
        if (duplicatedRequest) {
            throw new RestApiConnectionException(RestApiConnResponseCode.RCF10);
        }

        // 생성
        RestApiConnection restApiConnection = RestApiConnection.builder()
                .scheme(request.scheme())
                .host(request.host())
                .port(request.port())
                .methodType(request.methodType())
                .path(request.path())
                .requesterId(request.requesterId())
                .description(request.description())
                .triggerType(request.triggerType())
                .documentType(request.documentType())
                .used(true)
                .creteDateTime(LocalDateTime.now())
                .build();

        // 검증


        // 저장
        RestApiConnection savedRestApiConnection = restApiConnectionRepository.save(restApiConnection);
        return new RestApiConnectionResponse(
                savedRestApiConnection.getConnectionPk(),
                savedRestApiConnection.getDescription(),
                savedRestApiConnection.getScheme(),
                savedRestApiConnection.getHost(),
                savedRestApiConnection.getPort(),
                savedRestApiConnection.getMethodType(),
                savedRestApiConnection.getPath(),
                savedRestApiConnection.getTriggerType(),
                savedRestApiConnection.getDocumentType(),
                savedRestApiConnection.getCreateDateTime(),
                savedRestApiConnection.getRequesterId(),
                savedRestApiConnection.isUsed()
        );
    }
}
