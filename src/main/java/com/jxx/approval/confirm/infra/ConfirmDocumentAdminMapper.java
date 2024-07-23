package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.dto.request.ConfirmDocumentFormSearchCond;
import com.jxx.approval.confirm.dto.response.ConfirmDocumentFormResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ConfirmDocumentAdminMapper {

    List<ConfirmDocumentFormResponse> searchConfirmDocumentForm(ConfirmDocumentFormSearchCond searchCond);
}
