package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.wallet.domain.HwanbeeAccountLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HwanbeeLinkRepository extends JpaRepository<HwanbeeAccountLink, Long> {

  Optional<HwanbeeAccountLink> findByUserIdAndCurrencyCode(Long userId, String currencyCode);

  Optional<HwanbeeAccountLink> findByUserIdAndHwanbeeBankAccountNumber(Long userId, String hwanbeeAccountNumber);
}
