package com.jxx.approval.confirm.application.server.dto;

import java.util.List;

public record VerifyCompanyMemberDto(
        String companyId,
        List<String> membersId
) {
}
