package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.ConfirmDocument;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConfirmDocumentRepository extends JpaRepository<ConfirmDocument, Long> {

    boolean existsByPk(Long pk);

    Optional<ConfirmDocument> findByPk(Long pk);

    Optional<ConfirmDocument> findByDocumentConfirmDocumentId(String confirmDocumentId);

    @Query("select cd from ConfirmDocument cd " +
            "join fetch cd.content " +
            "where cd.content.pk =:contentPk")
    Optional<ConfirmDocument> findWithContent(@Param("contentPk") Long contentPk);

}
