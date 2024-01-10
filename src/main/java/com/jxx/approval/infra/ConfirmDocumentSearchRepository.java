package com.jxx.approval.infra;

import com.jxx.approval.dto.response.ConfirmServiceDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfirmDocumentSearchRepository {

    ConfirmServiceDto select(Long confirmDocumentPk);
}
