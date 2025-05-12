package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

  Optional<Wallet> findByUserIdAndCurrencyCode(Long userId, String currencyCode);

  boolean existsByUserIdAndCurrencyCode(Long userId, String currencyCode);

  @Query("SELECT w FROM Wallet w " +
    "JOIN FETCH w.currency " +
    "WHERE w.user.id = :userId")
  List<Wallet> findAllByUserWithCurrency(@Param("userId") Long userId);
}
