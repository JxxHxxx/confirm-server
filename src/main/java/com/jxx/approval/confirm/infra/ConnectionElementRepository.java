package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.connect.ConnectionElement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionElementRepository extends JpaRepository<ConnectionElement, Long> {
}
