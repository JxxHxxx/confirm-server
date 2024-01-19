package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.ConfirmDocumentForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmDocumentFormRepository extends JpaRepository<ConfirmDocumentForm, Long> {
}
