package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.document.ConfirmDocument;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


class ConfirmDocumentRepositoryTest {

    @Test
    void check_id() {
        ConfirmDocument confirmDocument = new ConfirmDocument(null,null,null,null,null,null);
        Long confirmDocumentPk = confirmDocument.getPk();

        assertThat(confirmDocumentPk).isNotNull();
    }

}