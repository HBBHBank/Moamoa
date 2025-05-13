package com.hbbhbank.moamoa.external.repository;

import com.hbbhbank.moamoa.external.domain.UserAccountLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HwanbeeLinkRepository extends JpaRepository<UserAccountLink, Long> {

  Optional<UserAccountLink> findByUserIdAndCurrencyCode(Long userId, String currencyCode);
}
