package com.jxx.approval.confirm.application;
import com.jxx.approval.confirm.domain.connect.RestApiConnResponseCode;
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
        public RestApiConnResponseCode call(ConfirmDocument confirmDocument, String triggerType) {
            return RestApiConnResponseCode.RCS01;
        }
    };
    @Autowired
    RestApiConnectionRepository restApiConnectionRepository;

    @Test
    void validate_identity_map() {
        System.out.println("Hello World");
    }

}