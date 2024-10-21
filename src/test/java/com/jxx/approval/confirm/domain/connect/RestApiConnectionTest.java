package com.jxx.approval.confirm.domain.connect;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


@Slf4j
class RestApiConnectionTest {

    @Test
    void valid_field_success_case() {
        RestApiConnection restApiConnection = RestApiConnection.builder()
                .methodType("POST")
                .scheme("HTTP")
                .path("/api/test")
                .triggerType("testTriggerType")
                .description("테스트 연동 API")
                .host("localhost")
                .port(8080)
                .build();

        String methodType = restApiConnection.getMethodType();
        String path = restApiConnection.getPath();
        String scheme = restApiConnection.getScheme();
        int port = restApiConnection.getPort();

        assertThat(RestApiConnection.validateMethodType(methodType)).isTrue();
        assertThat(RestApiConnection.validatePath(path)).isTrue();
        assertThat(RestApiConnection.validateScheme(scheme)).isTrue();
        assertThat(RestApiConnection.validatePort(port)).isTrue();
    }

    @Test
    void valid_field_fail_case() {
        RestApiConnection restApiConnection = RestApiConnection.builder()
                .methodType("KK") // KK 는 httpMethod 가 아님
                .scheme("FPT") // FPT 지원 안함
                .path("api/test") // 슬래쉬(/)로 시작하지 않음
                .triggerType("testTriggerType")
                .description("테스트 연동 API")
                .host("localhost")
                .port(66777) // 포트 범위를 벗어남
                .build();

        String methodType = restApiConnection.getMethodType();
        String path = restApiConnection.getPath();
        String scheme = restApiConnection.getScheme();
        int port = restApiConnection.getPort();

        assertThat(RestApiConnection.validateMethodType(methodType)).isFalse();
        assertThat(RestApiConnection.validatePath(path)).isFalse();
        assertThat(RestApiConnection.validateScheme(scheme)).isFalse();
        assertThat(RestApiConnection.validatePort(port)).isFalse();
    }
}