package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.external.domain.UserAccountLink;
import com.hbbhbank.moamoa.external.service.HwanbeeAccountService;
import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.global.security.util.SecurityUtil;
import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.user.exception.UserErrorCode;
import com.hbbhbank.moamoa.user.repository.UserRepository;
import com.hbbhbank.moamoa.wallet.domain.Currency;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.dto.request.CreateWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.WalletInquiryRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.WalletInquiryResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.WalletResponseDto;
import com.hbbhbank.moamoa.wallet.exception.WalletErrorCode;
import com.hbbhbank.moamoa.wallet.repository.CurrencyRepository;
import com.hbbhbank.moamoa.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

  private final WalletRepository walletRepository;
  private final UserRepository userRepository;
  private final CurrencyRepository currencyRepository;
  private final HwanbeeAccountService hwanbeeAccountService;

  /**
   * 사용자 본인의 통화별 지갑 정보 조회
   */
  public WalletInquiryResponseDto showWallet(WalletInquiryRequestDto req) {
    Long userId = SecurityUtil.getCurrentUserId();
    Wallet wallet = walletRepository.findByUserIdAndCurrencyCode(userId, req.currencyCode())
      .orElseThrow(() -> BaseException.type(WalletErrorCode.NOT_FOUND_WALLET));

    return WalletInquiryResponseDto.from(wallet);
  }

  /**
   * 사용자 전체 지갑 목록 조회
   */
  @Transactional
  public List<WalletInquiryResponseDto> getAllWalletsByUser() {
    Long userId = SecurityUtil.getCurrentUserId();
    return walletRepository.findAllByUserWithCurrency(userId)
      .stream()
      .map(WalletInquiryResponseDto::from)
      .collect(Collectors.toList());
  }

  /**
   * 통화별 지갑 생성 (외부 계좌 인증 포함)
   */
  @Transactional
  public WalletResponseDto createWallet(CreateWalletRequestDto req) {
    Long userId = SecurityUtil.getCurrentUserId();

    // 1. 사용자 조회
    User user = userRepository.findById(userId)
      .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

    // 2. 통화 조회
    Currency currency = currencyRepository.findByCode(req.currencyCode())
      .orElseThrow(() -> BaseException.type(WalletErrorCode.CURRENCY_CODE_NOT_FOUND));

    // 3. 지갑 중복 확인
    if (walletRepository.existsByUserIdAndCurrencyCode(userId, currency.getCode())) {
      throw BaseException.type(WalletErrorCode.DUPLICATE_WALLET);
    }

    // 4. 외부 계좌 인증 및 연결
    UserAccountLink accountLink = hwanbeeAccountService.verifyAndLinkAccount(
      user,
      req.externalAccountNumber(),
      req.verificationCode()
    );

    // 5. 지갑 계좌번호 생성
    String accountNumber = generateAccountNumber(userId, currency.getCode());

    // 6. 지갑 생성 및 저장
    Wallet wallet = Wallet.builder()
      .user(user)
      .currency(currency)
      .accountNumber(accountNumber)
      .accountLink(accountLink)
      .balance(BigDecimal.ZERO)
      .build();

    return WalletResponseDto.from(walletRepository.save(wallet));
  }

  /**
   * 지갑 계좌번호 생성 규칙: [통화코드]-[사용자ID]-[8자리랜덤숫자]
   */
  private String generateAccountNumber(Long userId, String currencyCode) {
    int randomNumber = (int) (Math.random() * 90000000) + 10000000;
    return String.format("%s-%d-%d", currencyCode, userId, randomNumber);
  }
}
