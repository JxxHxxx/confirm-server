package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.dto.request.ConfirmDocumentSearchCondition;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentWithApprovalLineResponse;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentServiceResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConfirmDocumentMapper {

    ConfirmDocumentServiceResponse select(@Param("confirmDocumentId") Long confirmDocumentPk);

    List<ConfirmDocumentServiceResponse> search(ConfirmDocumentSearchCondition condition);

    List<ConfirmDocumentWithApprovalLineResponse> fetchWithApprovalLine(ConfirmDocumentSearchCondition condition);
}
