package com.jxx.approval.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmDocumentRepository extends JpaRepository<ConfirmDocument, Long> {

    Optional<ConfirmDocument> findByConfirmDocumentId(String confirmDocumentId);
}
