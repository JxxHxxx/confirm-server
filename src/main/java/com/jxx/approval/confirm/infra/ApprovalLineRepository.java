package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.ApprovalLine;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, Long> {

    List<ApprovalLine> findByConfirmDocumentConfirmDocumentId(String confirmDocumentId);
    @Query("SELECT al FROM ApprovalLine al WHERE al.confirmDocument.confirmDocumentId =:confirmDocumentId")
    List<ApprovalLine> findByConfirmDocumentId(@Param("confirmDocumentId") String confirmDocumentId);

}
