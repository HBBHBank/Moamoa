package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.wallet.domain.ExternalWalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.InternalWalletTransaction;
import com.hbbhbank.moamoa.wallet.dto.request.transaction.TransactionFilterRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.transaction.TransactionResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static com.hbbhbank.moamoa.wallet.domain.QInternalWalletTransaction.internalWalletTransaction;
import static com.hbbhbank.moamoa.wallet.domain.QExternalWalletTransaction.externalWalletTransaction;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {

  private final JPAQueryFactory queryFactory;

  // 전체/지갑별/기간별/구분별 모두 처리
  @Override
  public Page<TransactionResponseDto> findAll(TransactionFilterRequestDto filter) {

    // 내부 거래 조건 빌드
    BooleanBuilder internalCond = new BooleanBuilder();

    if (filter.walletId() != null)
      internalCond.and(internalWalletTransaction.wallet.id.eq(filter.walletId()));
    if (filter.currencyCode() != null)
      internalCond.and(internalWalletTransaction.wallet.currency.code.eq(filter.currencyCode()));
    if (filter.type() != null)
      internalCond.and(internalWalletTransaction.type.eq(filter.type()));
    if (filter.startDate() != null && filter.endDate() != null)
      internalCond.and(internalWalletTransaction.transactedAt.between(filter.startDate(), filter.endDate()));

    // 외부 거래 조건 빌드
    BooleanBuilder externalCond = new BooleanBuilder();
    if (filter.walletId() != null)
      externalCond.and(externalWalletTransaction.wallet.id.eq(filter.walletId()));
    if (filter.currencyCode() != null)
      externalCond.and(externalWalletTransaction.wallet.currency.code.eq(filter.currencyCode()));
    if (filter.type() != null)
      externalCond.and(externalWalletTransaction.type.eq(filter.type()));
    if (filter.startDate() != null && filter.endDate() != null)
      externalCond.and(externalWalletTransaction.transactedAt.between(filter.startDate(), filter.endDate()));

    // 내부 거래 조회
    List<TransactionResponseDto> internals = queryFactory
      .selectFrom(internalWalletTransaction)
      .where(internalCond)
      .orderBy(internalWalletTransaction.transactedAt.desc())
      .fetch()
      .stream()
      .map(tx -> new TransactionResponseDto(
        tx.getId(),
        tx.getWallet().getWalletNumber(),
        tx.getCounterWallet() != null ? tx.getCounterWallet().getWalletNumber() : null,
        tx.getWallet().getCurrency().getCode(),
        tx.getType(),
        tx.getStatus(),
        tx.getAmount(),
        tx.getTransactedAt(),
        false
      )).toList();

    // 외부 거래 조회
    List<TransactionResponseDto> externals = queryFactory
      .selectFrom(externalWalletTransaction)
      .where(externalCond)
      .orderBy(externalWalletTransaction.transactedAt.desc())
      .fetch()
      .stream()
      .map(tx -> new TransactionResponseDto(
        tx.getId(),
        tx.getWallet().getWalletNumber(),
        null,
        tx.getWallet().getCurrency().getCode(),
        tx.getType(),
        tx.getStatus(),
        tx.getAmount(),
        tx.getTransactedAt(),
        true
      )).toList();

    // 통합 정렬 (최신순) 후 페이징
    List<TransactionResponseDto> merged = Stream.concat(internals.stream(), externals.stream())
      .sorted(Comparator.comparing(TransactionResponseDto::transactedAt).reversed())
      .toList();

    int page = filter.page() != null ? filter.page() : 0;
    int size = filter.size() != null ? filter.size() : 20;
    int start = Math.min(page * size, merged.size());
    int end = Math.min(start + size, merged.size());

    return new PageImpl<>(merged.subList(start, end), PageRequest.of(page, size), merged.size());
  }

  // 최신 거래 1건 조회
  @Override
  public TransactionResponseDto findLatest() {
    InternalWalletTransaction internal = queryFactory
      .selectFrom(internalWalletTransaction)
      .orderBy(internalWalletTransaction.transactedAt.desc())
      .limit(1)
      .fetchOne();

    ExternalWalletTransaction external = queryFactory
      .selectFrom(externalWalletTransaction)
      .orderBy(externalWalletTransaction.transactedAt.desc())
      .limit(1)
      .fetchOne();

    TransactionResponseDto iRes = internal == null ? null :
      new TransactionResponseDto(
        internal.getId(),
        internal.getWallet().getWalletNumber(),
        internal.getCounterWallet() != null ? internal.getCounterWallet().getWalletNumber() : null,
        internal.getWallet().getCurrency().getCode(),
        internal.getType(),
        internal.getStatus(),
        internal.getAmount(),
        internal.getTransactedAt(),
        false
      );

    TransactionResponseDto eRes = external == null ? null :
      new TransactionResponseDto(
        external.getId(),
        external.getWallet().getWalletNumber(),
        null,
        external.getWallet().getCurrency().getCode(),
        external.getType(),
        external.getStatus(),
        external.getAmount(),
        external.getTransactedAt(),
        true
      );

    if (iRes == null && eRes == null) return null;
    if (iRes == null) return eRes;
    if (eRes == null) return iRes;
    return iRes.transactedAt().isAfter(eRes.transactedAt()) ? iRes : eRes;
  }
}
