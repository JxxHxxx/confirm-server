package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.dto.request.ConfirmDocumentSearchCondition;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentWithApprovalLineResponse;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentServiceResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConfirmDocumentMapper {

    List<ConfirmDocumentServiceResponse> search(ConfirmDocumentSearchCondition condition);

    List<ConfirmDocumentServiceResponse> findByConfirmDocumentId(@Param("confirmDocumentId") String confirmDocumentId);

    List<ConfirmDocumentWithApprovalLineResponse> fetchWithApprovalLine(ConfirmDocumentSearchCondition condition);
}
