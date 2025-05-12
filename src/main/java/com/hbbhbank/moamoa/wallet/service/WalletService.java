package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.external.domain.UserAccountLink;
import com.hbbhbank.moamoa.external.dto.request.GenerateVerificationCodeRequestDto;
import com.hbbhbank.moamoa.external.dto.request.VerificationCheckRequestDto;
import com.hbbhbank.moamoa.external.dto.response.VerificationCodeResponseDto;
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

  private final WalletRepository    walletRepository;
  private final UserRepository      userRepository;
  private final CurrencyRepository  currencyRepository;
  private final HwanbeeAccountService hwanbeeAccountService;

  // ------------------------------------------------------------
  // 1) 단일 지갑 조회
  // ------------------------------------------------------------
  public WalletInquiryResponseDto showWallet(WalletInquiryRequestDto req) {
    Long userId = SecurityUtil.getCurrentUserId();

    Wallet found = walletRepository.findByUserIdAndCurrencyCode(userId, req.currencyCode())
      .orElseThrow(() -> BaseException.type(WalletErrorCode.NOT_FOUND_WALLET));

    return WalletInquiryResponseDto.from(found);
  }

  // ------------------------------------------------------------
  // 2) 전체 지갑 목록 조회
  // ------------------------------------------------------------
  @Transactional
  public List<WalletInquiryResponseDto> getAllWalletsByUser() {
    Long userId = SecurityUtil.getCurrentUserId();

    return walletRepository.findAllByUserWithCurrency(userId).stream()
      .map(WalletInquiryResponseDto::from)
      .collect(Collectors.toList());
  }

  // ------------------------------------------------------------
  // 3) 외부 계좌 인증코드 발급 요청
  // ------------------------------------------------------------
  public VerificationCodeResponseDto requestVerificationCode(GenerateVerificationCodeRequestDto req) {
    Long userId = SecurityUtil.getCurrentUserId();

    // 3-1. 통화 유효성 검증
    currencyRepository.findByCode(req.currencyCode())
      .orElseThrow(() -> BaseException.type(WalletErrorCode.CURRENCY_CODE_NOT_FOUND));

    // 3-2. 환비 API 호출
    return hwanbeeAccountService.generateVerificationCode(
      new GenerateVerificationCodeRequestDto(
        userId,
        req.externalBankAccountNumber(),
        req.currencyCode()
      )
    );
  }

  // ------------------------------------------------------------
  // 4) 인증코드 검증 → 계좌연결 → 내부 지갑 생성
  // ------------------------------------------------------------
  @Transactional
  public WalletResponseDto createWallet(CreateWalletRequestDto req) {
    Long userId = SecurityUtil.getCurrentUserId();

    // 4-1. 사용자·통화 검증
    User user = userRepository.findById(userId)
      .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

    Currency currency = currencyRepository.findByCode(req.currencyCode())
      .orElseThrow(() -> BaseException.type(WalletErrorCode.CURRENCY_CODE_NOT_FOUND));

    // 4-2. 중복 지갑 방지
    if (walletRepository.existsByUserIdAndCurrencyCode(userId, currency.getCode())) {
      throw BaseException.type(WalletErrorCode.DUPLICATE_WALLET);
    }

    // 4-3. 인증코드 검증 및 외부 계좌 링크 생성
    UserAccountLink accountLink = hwanbeeAccountService.verifyAndLinkAccount(
      new VerificationCheckRequestDto(
        userId,
        req.externalAccountNumber(),
        req.verificationCode()
      )
    );

    // 4-4. 내부용 지갑 번호 생성
    String walletNumber = generateWalletNumber(userId, currency.getCode());

    // 4-5. 지갑 엔티티 생성·저장
    Wallet wallet = Wallet.builder()
      .user(user)
      .currency(currency)
      .accountNumber(walletNumber)
      .accountLink(accountLink)
      .balance(BigDecimal.ZERO)
      .build();

    Wallet saved = walletRepository.save(wallet);
    return WalletResponseDto.from(saved);
  }

  // ------------------------------------------------------------
  // [utils] 지갑번호 랜덤 생성: [통화]-[유저ID]-[8자리 난수]
  // ------------------------------------------------------------
  private String generateWalletNumber(Long userId, String currencyCode) {
    int random = (int)(Math.random() * 90_000_000) + 10_000_000;
    return String.format("%s-%d-%08d", currencyCode, userId, random);
  }
}
