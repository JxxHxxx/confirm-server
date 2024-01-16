package com.jxx.approval.infra;

import com.jxx.approval.domain.ConfirmDocumentForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmDocumentFormRepository extends JpaRepository<ConfirmDocumentForm, Long> {
}
