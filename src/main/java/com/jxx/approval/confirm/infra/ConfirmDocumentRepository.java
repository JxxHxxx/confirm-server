package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.ConfirmDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConfirmDocumentRepository extends JpaRepository<ConfirmDocument, Long> {

    boolean existsByPk(Long pk);

    Optional<ConfirmDocument> findByPk(Long pk);

    Optional<ConfirmDocument> findByDocumentConfirmDocumentId(String confirmDocumentId);

}
