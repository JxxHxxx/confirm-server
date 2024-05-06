package com.jxx.approval.confirm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor
public class Requester {
    @Column(name = "COMPANY_ID", nullable = false)
    @Comment(value = "요청자 회사 ID")
    private String companyId;

    @Column(name = "DEPARTMENT_ID", nullable = false)
    @Comment(value = "요청자 부서 ID")
    private String departmentId;

    @Column(name = "REQUESTER_ID", nullable = false)
    @Comment(value = "결재 요청자 ID")
    private String requesterId;

    public Requester(String companyId, String departmentId, String requesterId) {
        this.companyId = companyId;
        this.departmentId = departmentId;
        this.requesterId = requesterId;
    }

    public boolean isNotRequester(String requesterId) {
        return !this.requesterId.equals(requesterId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Requester requester = (Requester) o;
        return Objects.equals(companyId, requester.companyId) && Objects.equals(departmentId, requester.departmentId) && Objects.equals(requesterId, requester.requesterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyId, departmentId, requesterId);
    }
}
