package com.jxx.approval.confirm.domain.connect;

import com.jxx.approval.confirm.domain.connect.dto.CreateMappingConfirmApiRequest;

/**
 * RestApiConnection 을 만들기 위한 요청 파라미터가 유효한지 검증
 */
public class RestApiConnectionValidator {

    /**
     * @Return
       - true : 유효하지 않음
       - false : 유효함
     **/
    public static boolean notValid(CreateMappingConfirmApiRequest request) {
        return false;
    }
}
