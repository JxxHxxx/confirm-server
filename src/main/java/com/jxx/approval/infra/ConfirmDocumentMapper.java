package com.jxx.approval.infra;

import com.jxx.approval.domain.ConfirmDocument;
import com.jxx.approval.dto.request.ConfirmDocumentSearchCondition;
import com.jxx.approval.dto.response.ConfirmDocumentServiceResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConfirmDocumentMapper {

    ConfirmDocumentServiceResponse select(@Param("confirmDocumentPk") Long confirmDocumentPk);

    void save(ConfirmDocument confirmDocument);

    List<ConfirmDocumentServiceResponse> search(ConfirmDocumentSearchCondition condition);
}
