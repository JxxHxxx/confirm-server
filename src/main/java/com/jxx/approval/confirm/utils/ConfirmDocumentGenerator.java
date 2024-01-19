package com.jxx.approval.confirm.utils;

import com.jxx.approval.confirm.domain.ConfirmDocument;
import com.jxx.approval.confirm.domain.ConfirmStatus;
import com.jxx.approval.confirm.domain.DocumentType;
import com.jxx.approval.confirm.domain.Requester;
import com.jxx.approval.confirm.dto.request.Document;
import lombok.Getter;

import java.util.List;
import java.util.Random;

public class ConfirmDocumentGenerator {

    public static ConfirmDocument execute() {
        Organization organization = organization();
        return ConfirmDocument.builder()
                .document(new Document(documentType()))
                .requester(new Requester(organization.getCompanyId(), organization.getDepartmentId(), organization.getRequesterId()))
                .confirmStatus(ConfirmStatus.CREATE)
                .createSystem("API")
                .build();

    }


    private static DocumentType documentType() {
        return new Random().nextInt(2) == 0 ? DocumentType.VAC : DocumentType.DCR;
    }


    private static List<String> companies = List.of("JXX", "XUNI");
    private static List<String> jxxDepartments = List.of("J00001", "J00002", "J00003", "J00004");
    private static List<String> xuniDepartments = List.of("X10001", "X20001", "X30001", "X40001");
    private static List<String> jxxRequesters = List.of("J20230001", "J20230002", "J20230003", "J20230004",
            "J20230005", "J20230006", "J20230007", "J20230008", "J20230009", "J20240001", "J20240002", "J20240003",
            "J20240004", "J20240005", "J20240006", "J20240007", "J20240008", "J20240009", "J20240010", "J20240011");
    private static List<String> xuniRequesters = List.of("X0000001", "X0000002", "X0000003",
            "X0000004", "X0000005", "X0000006", "X0000007", "X0000008", "X0000009", "X0000010", "X0000011",
            "X0000012", "X0000013", "X0000014", "X0000015", "X0000016", "X0000017", "X0000018", "X0000019");

    @Getter
    static class Organization {
        private String companyId;
        private String departmentId;
        private String requesterId;

        public Organization(String companyId, String departmentId, String requesterId) {
            this.companyId = companyId;
            this.departmentId = departmentId;
            this.requesterId = requesterId;
        }
    }

    private static Organization organization() {
        int idx = new Random().nextInt(2);
        String companyId = companies.get(idx);

        String departmentId;
        String requesterId;
        if ("JXX".equals(companyId)) {
            int result = new Random().nextInt(jxxDepartments.size());
            departmentId = jxxDepartments.get(result);
            int result2 = new Random().nextInt(jxxRequesters.size());
            requesterId = jxxRequesters.get(result2);
        } else {
            int result = new Random().nextInt(xuniDepartments.size());
            departmentId = xuniDepartments.get(result);
            int result2 = new Random().nextInt(xuniRequesters.size());
            requesterId = xuniRequesters.get(result2);
        }

        return new Organization(companyId, departmentId, requesterId);
    }
}
