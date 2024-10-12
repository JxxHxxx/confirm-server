package com.jxx.approval.confirm.application;
import com.jxx.approval.confirm.domain.document.ConfirmDocument;
import com.jxx.approval.confirm.infra.RestApiConnectionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class ConfirmDocumentServiceTest {

    @Autowired
    ConfirmDocumentService confirmDocumentService;

    ConfirmDocumentRestApiAdapterService confirmDocumentRestApiAdapterService = new ConfirmDocumentRestApiAdapterService() {
        @Override
        public boolean call(ConfirmDocument confirmDocument, String triggerType) {
            return true;
        }
    };
    @Autowired
    RestApiConnectionRepository restApiConnectionRepository;

    @Test
    void validate_identity_map() {
        System.out.println("Hello World");
    }

}