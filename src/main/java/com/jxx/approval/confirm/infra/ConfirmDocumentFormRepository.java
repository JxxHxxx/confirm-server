package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.form.ConfirmDocumentForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConfirmDocumentFormRepository extends JpaRepository<ConfirmDocumentForm, Long> {

    Optional<ConfirmDocumentForm> findByFormIdAndCompanyId(String formId, String companyId);

    List<ConfirmDocumentForm> findByCompanyId(String companyId);

    @Query("select cdf from ConfirmDocumentForm cdf " +
            "where cdf.formName like concat('%',:formName, '%') ")
    List<ConfirmDocumentForm> searchConfirmDocumentForm(@Param("formName") String formName);
}
