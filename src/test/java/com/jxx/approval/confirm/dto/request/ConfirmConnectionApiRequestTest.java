package com.jxx.approval.confirm.dto.request;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;


/**
 *
 */
class ConfirmConnectionApiRequestTest {

    @Test
    void pathRegexpTest() {
        Pattern.matches("^./*", "123");
    }

}