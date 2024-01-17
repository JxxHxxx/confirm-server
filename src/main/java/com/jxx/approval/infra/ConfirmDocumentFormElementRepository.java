package com.jxx.approval.infra;

import com.jxx.approval.domain.ConfirmDocumentFormElement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfirmDocumentFormElementRepository extends JpaRepository<ConfirmDocumentFormElement, Long> {

    List<ConfirmDocumentFormElement> findByConfirmDocumentFormPk(Long formPk);


}
