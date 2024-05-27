package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.ConfirmDocumentForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmDocumentFormRepository extends JpaRepository<ConfirmDocumentForm, Long> {

    Optional<ConfirmDocumentForm> findByFormId(String formId);
}
