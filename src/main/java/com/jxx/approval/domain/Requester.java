package com.jxx.approval.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Embeddable
@NoArgsConstructor
public class Requester {
    @Column(name = "COMPANY_ID", nullable = false)
    @Comment(value = "회사 ID")
    private String companyId;

    @Column(name = "DEPARTMENT_ID", nullable = false)
    @Comment(value = "부서 ID")
    private String departmentId;

    @Column(name = "REQUESTER_ID", nullable = false)
    @Comment(value = "결재 문서 생성자 ID")
    private String requesterId;

    public Requester(String companyId, String departmentId, String requesterId) {
        this.companyId = companyId;
        this.departmentId = departmentId;
        this.requesterId = requesterId;
    }
}
