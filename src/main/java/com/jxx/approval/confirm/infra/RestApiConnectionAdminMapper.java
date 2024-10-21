package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.dto.request.RestApiConnectionSearchCond;
import com.jxx.approval.confirm.dto.response.RestApiConnectionResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RestApiConnectionAdminMapper {

    List<RestApiConnectionResponse> search(RestApiConnectionSearchCond cond);
}
