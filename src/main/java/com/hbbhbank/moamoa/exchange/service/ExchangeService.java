package com.hbbhbank.moamoa.exchange.service;

import com.hbbhbank.moamoa.exchange.dto.request.ExchangePointRequestDto;
import com.hbbhbank.moamoa.exchange.dto.response.ExchangePointResponseDto;
import com.hbbhbank.moamoa.exchange.exception.ExchangeErrorCode;
import com.hbbhbank.moamoa.external.dto.request.ExchangeDealRequestDto;
import com.hbbhbank.moamoa.external.dto.request.ExchangeQuoteRequestDto;
import com.hbbhbank.moamoa.external.dto.response.ExchangeDealResponseDto;
import com.hbbhbank.moamoa.external.dto.response.ExchangeQuoteResponseDto;
import com.hbbhbank.moamoa.external.service.HwanbeeExchangeService;
import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.global.security.util.SecurityUtil;
import com.hbbhbank.moamoa.wallet.domain.Currency;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;
import com.hbbhbank.moamoa.wallet.repository.WalletRepository;
import com.hbbhbank.moamoa.wallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExchangeService {

  private final HwanbeeExchangeService hwanbeeExchangeService;
  private final WalletRepository walletRepository;
  private final WalletTransactionRepository walletTransactionRepository;

  @Transactional
  public ExchangePointResponseDto exchange(ExchangePointRequestDto dto) {
    Long userId = SecurityUtil.getCurrentUserId();

    // 1. 지갑 조회
    Wallet fromWallet = walletRepository.findByUserIdAndCurrencyCode(userId, dto.fromCurrency())
      .orElseThrow(() -> BaseException.type(ExchangeErrorCode.NOT_FOUND_FROM_WALLET));
    Wallet toWallet = walletRepository.findByUserIdAndCurrencyCode(userId, dto.toCurrency())
      .orElseThrow(() -> BaseException.type(ExchangeErrorCode.NOT_FOUND_TO_WALLET));

    Currency fromCurrency = fromWallet.getCurrency();
    Currency toCurrency = toWallet.getCurrency();

    // 2. 금액 유효성 검사 (환전 단위로 나누어떨어져야 함)
    BigDecimal unit = fromCurrency.getUnit(); // CurrencyUnit enum에서 가져옴
    if (dto.amount().remainder(unit).compareTo(BigDecimal.ZERO) != 0) {
      throw BaseException.type(ExchangeErrorCode.INVALID_AMOUNT);
    }

    // 3. 환비 API: 환율 견적 요청
    ExchangeQuoteRequestDto quoteRequest = new ExchangeQuoteRequestDto(
      dto.fromCurrency(),
      dto.toCurrency(),
      dto.amount().toPlainString()
    );
    ExchangeQuoteResponseDto quoteResponse = hwanbeeExchangeService.showExchangeRate(quoteRequest);

    // 4. 환비 API: 환전 체결
    ExchangeDealRequestDto dealRequest = new ExchangeDealRequestDto(
      quoteResponse.quoteId(),
      dto.amount().toPlainString(),
      UUID.randomUUID().toString()
    );
    ExchangeDealResponseDto dealResponse = hwanbeeExchangeService.exchange(dealRequest);

    // 5. fromWallet 차감
    fromWallet.updateBalance(dto.amount());

    // 6. toWallet 증가
    toWallet.subtractBalance(dealResponse.exchangedAmount());

    // 7. 거래 내역 기록
    walletTransactionRepository.save(WalletTransaction.builder()
      .wallet(fromWallet)
      .counterWallet(toWallet)
      .type(WalletTransactionType.EXCHANGE_OUT)
      .amount(dto.amount())
      .includedInSettlement(false)
      .build());

    walletTransactionRepository.save(WalletTransaction.builder()
      .wallet(toWallet)
      .counterWallet(fromWallet)
      .type(WalletTransactionType.EXCHANGE_IN)
      .amount(dealResponse.exchangedAmount())
      .includedInSettlement(false)
      .build());

    // 8. 응답 반환
    return new ExchangePointResponseDto(
      dto.fromCurrency(),
      dto.toCurrency(),
      dto.amount(),
      dealResponse.exchangedAmount(),
      dealResponse.executedRate(),
      dealResponse.dealtAt()
    );
  }
}
