package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.ConfirmDocumentContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmDocumentContentRepository extends JpaRepository<ConfirmDocumentContent, Long> {
}
