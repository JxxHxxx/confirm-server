package com.jxx.approval.confirm.infra;

import com.jxx.approval.confirm.domain.connect.ConnectionParameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionParameterRepository extends JpaRepository<ConnectionParameter, Long> {
}
