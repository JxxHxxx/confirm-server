package com.jxx.approval.infra;

import com.jxx.approval.domain.ConfirmDocumentContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmDocumentContentRepository extends JpaRepository<ConfirmDocumentContent, Long> {
}
