package com.jxx.approval.infra;

import com.jxx.approval.domain.ConfirmDocumentFormElement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmDocumentFormElementRepository extends JpaRepository<ConfirmDocumentFormElement, Long> {
}
