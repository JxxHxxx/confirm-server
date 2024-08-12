package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.form.ConfirmDocumentElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConfirmDocumentElementRepository extends JpaRepository<ConfirmDocumentElement, Long> {
    @Query("select cde from ConfirmDocumentElement cde " +
            "where cde.confirmDocumentForm.companyId in :companyIds " +
            "and cde.confirmDocumentForm.formId=:formId")
    List<ConfirmDocumentElement> findByConfirmDocumentForms(List<String> companyIds, String formId);
}
