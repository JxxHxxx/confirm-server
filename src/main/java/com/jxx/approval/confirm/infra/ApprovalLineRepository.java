package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.line.ApprovalLine;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, Long> {

    @Query("SELECT al FROM ApprovalLine al join fetch al.confirmDocument WHERE al.confirmDocument.confirmDocumentId =:confirmDocumentId")
    List<ApprovalLine> fetchByConfirmDocumentId(@Param("confirmDocumentId") String confirmDocumentId);
    @Query("SELECT al FROM ApprovalLine al WHERE al.confirmDocument.confirmDocumentId =:confirmDocumentId")
    List<ApprovalLine> findByConfirmDocumentId(@Param("confirmDocumentId") String confirmDocumentId);

}
