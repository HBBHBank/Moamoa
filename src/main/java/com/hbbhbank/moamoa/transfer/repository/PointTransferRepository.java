package com.hbbhbank.moamoa.transfer.repository;

import com.hbbhbank.moamoa.transfer.domain.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointTransferRepository extends JpaRepository<Transfer, Long> {
}
