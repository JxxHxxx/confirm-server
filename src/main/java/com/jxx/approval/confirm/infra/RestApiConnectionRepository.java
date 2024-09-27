package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.connect.RestApiConnection;
import com.jxx.approval.confirm.domain.document.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestApiConnectionRepository extends JpaRepository<RestApiConnection, Long> {

    Optional<RestApiConnection> findByDocumentTypeAndTriggerType(DocumentType documentType, String triggerType);
}
