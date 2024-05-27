package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.ConfirmDocumentFormElement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfirmDocumentFormElementRepository extends JpaRepository<ConfirmDocumentFormElement, Long> {

    List<ConfirmDocumentFormElement> findByConfirmDocumentForm(String confirmDocumentId);


}
