package com.jxx.approval.confirm.domain.connect;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class RestApiConnResponseCodeTest {

    @Test
    void toStringTest() {
        String s = RestApiConnResponseCode.RCF01.toString();
        log.info("result {}", s);
    }

}