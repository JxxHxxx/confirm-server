package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.ApprovalLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, Long> {

    List<ApprovalLine> findByConfirmDocumentPk(Long confirmDocumentPk);
    List<ApprovalLine> findByConfirmDocument(Long confirmDocumentPk);
}
