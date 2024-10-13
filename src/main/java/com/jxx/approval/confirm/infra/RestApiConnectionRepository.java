package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.connect.RestApiConnection;
import com.jxx.approval.confirm.domain.document.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestApiConnectionRepository extends JpaRepository<RestApiConnection, Long> {

    @Query("select rac from RestApiConnection rac join fetch rac.connectionElements " +
            "where rac.documentType =:documentType and rac.triggerType =:triggerType")
    List<RestApiConnection> fetchWithConnectionElements(@Param("documentType") DocumentType documentType, @Param("triggerType") String triggerType);
}
