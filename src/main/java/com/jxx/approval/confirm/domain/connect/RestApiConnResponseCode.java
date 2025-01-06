package com.jxx.approval.confirm.domain.connect;


import lombok.Getter;

import java.util.List;

// 코드 구성 : RC(prefix) + S or F(응답 성공/실패 여부) + XX(상태 코드 구분을 위한 숫자 두자리)
@Getter
public enum RestApiConnResponseCode {
    RCS01("응답 성공", true),
    RCS02("연동 API 가 등록되어 있지만 사용하지 않음", true),
    RCF01("연동 API 에서 2xx 응답을 내리지 않음", false),
    RCF02("조건을 만족하는 연동 API 조회가 되지 않음", false),
    RCF10("연동 API 등록 실패 - 중복 등록(DocumentType, TriggerType)", false),
    RCF90("예상치 못한 오류 개발자, 운영자의 확인이 필요", false),
    RCF99("미구현", false);

    private String description;
    private boolean success;

    RestApiConnResponseCode(String description, boolean success) {
        this.description = description;
        this.success = success;
    }

    public static List<RestApiConnResponseCode> FAIL_GROUP = List.of(RCF01, RCF02, RCF90, RCF99);
}
