package com.jxx.approval.infra;

import com.jxx.approval.domain.Approver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApproverRepository extends JpaRepository<Approver, Long> {

    Optional<Approver> findByConfirmDocument(Long confirmDocumentPk);
}
