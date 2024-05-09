package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.ConfirmDocument;
import com.jxx.approval.confirm.dto.request.ConfirmDocumentSearchConditionQueryString;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentWithApprovalLineResponse;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentServiceResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConfirmDocumentMapper {

    ConfirmDocumentServiceResponse select(@Param("confirmDocumentId") Long confirmDocumentPk);

    void save(ConfirmDocument confirmDocument);

    List<ConfirmDocumentWithApprovalLineResponse> fetchWithApprovalLine(ConfirmDocumentSearchConditionQueryString condition);
}
