package com.jxx.approval.infra;

import com.jxx.approval.domain.ConfirmDocumentElement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfirmDocumentElementRepository extends JpaRepository<ConfirmDocumentElement, Long> {

    List<ConfirmDocumentElement> findByPkIn(List<Long> elementPks);
}
