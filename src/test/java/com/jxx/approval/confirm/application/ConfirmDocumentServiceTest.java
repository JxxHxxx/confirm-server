package com.jxx.approval.confirm.application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;


class ConfirmDocumentServiceTest {

    @Test
    void validate_identity_map() {

        Map<String, Object> original = new HashMap<>();
        original.put("name", "jh");

        Map<String, Object> copy =  new HashMap<>(original);

        original.put("age", 30);
        System.out.println("aa");

        assertThat(original).isEqualTo(copy);
        assertThat(copy.get("age")).isEqualTo(30);
    }

}