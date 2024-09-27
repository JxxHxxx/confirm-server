package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.connect.ConfirmDocumentConnection;
import com.jxx.approval.confirm.domain.document.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmDocumentConnectionRepository extends JpaRepository<ConfirmDocumentConnection, Long> {

    Optional<ConfirmDocumentConnection> findByDocumentTypeAndTriggerType(DocumentType documentType, String triggerType);
}
