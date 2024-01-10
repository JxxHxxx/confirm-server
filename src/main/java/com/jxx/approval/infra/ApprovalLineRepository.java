package com.jxx.approval.infra;

import com.jxx.approval.domain.ApprovalLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, Long> {

    Optional<ApprovalLine> findByConfirmDocument(Long confirmDocumentPk);
}
