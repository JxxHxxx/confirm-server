package com.jxx.approval.confirm;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;


class DefaultConfirmDocumentRestApiAdapterServiceTest {

    @Test
    void ListEmptyTest() {
        List<String> emptyList = List.of();

        Assertions.assertThatThrownBy(() -> emptyList.get(0))
                .isInstanceOf(ArrayIndexOutOfBoundsException.class);
    }
}