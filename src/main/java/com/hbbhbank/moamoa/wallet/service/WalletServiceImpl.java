package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.external.client.HwanbeeAccountClient;
import com.hbbhbank.moamoa.external.dto.request.account.VerificationCheckRequestDto;
import com.hbbhbank.moamoa.external.dto.request.account.VerificationCodeRequestDto;
import com.hbbhbank.moamoa.external.dto.response.account.VerificationAccountDataDto;
import com.hbbhbank.moamoa.external.dto.response.account.VerificationCheckResponseDto;
import com.hbbhbank.moamoa.external.dto.response.account.VerificationCodeResponseDto;
import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.user.service.UserService;
import com.hbbhbank.moamoa.wallet.domain.AccountVerificationRequest;
import com.hbbhbank.moamoa.wallet.domain.Currency;
import com.hbbhbank.moamoa.wallet.domain.HwanbeeAccountLink;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.dto.request.wallet.SearchWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.wallet.CreateWalletResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.wallet.GetWalletInfoResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.wallet.SearchWalletResponseDto;
import com.hbbhbank.moamoa.wallet.exception.WalletErrorCode;
import com.hbbhbank.moamoa.wallet.repository.AccountVerificationRequestRepository;
import com.hbbhbank.moamoa.wallet.repository.HwanbeeLinkRepository;
import com.hbbhbank.moamoa.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

  private final WalletRepository walletRepository;
  private final UserService userService;
  private final HwanbeeAccountClient hwanbeeAccountClient;
  private final AccountVerificationRequestRepository accountVerificationRequestRepository;
  private final HwanbeeLinkRepository hwanbeeLinkRepository;
  private final CurrencyService currencyService;

  /**
   * 환비에 인증코드 발급 요청
   */
  @Override
  public void requestVerificationCode(VerificationCodeRequestDto req) {

    // 1. 환비 API에 인증코드 요청
    VerificationCodeResponseDto response = hwanbeeAccountClient.requestVerificationCode(req);

    // 2. 추후 인증코드 확인을 위해 응답에서 transactionId 추출
    String transactionId = response.data() != null ? response.data().transactionId() : null;

    if (transactionId == null) {
      throw new BaseException(WalletErrorCode.FAIL_VERIFICATION);
    }

    // 3. DB에 transactionId 저장
    accountVerificationRequestRepository.save(AccountVerificationRequest.from(transactionId));
  }

  /**
   * 외부 계좌 인증이 완료된 후 지갑 생성
   */
  @Override
  @Transactional
  public CreateWalletResponseDto createWalletAfterVerification(Integer inputCode) {
    Long userId = userService.getCurrentUserId();

    // 1. 가장 최근 인증 요청(transactionId) 찾기
    AccountVerificationRequest verificationRequest = accountVerificationRequestRepository
      .findByUserId(userId)
      .orElseThrow(() -> new BaseException(WalletErrorCode.FAIL_VERIFICATION));
    String transactionId = verificationRequest.getTransactionId();

    // 2. 환비 API에 인증 코드 검증 요청
    VerificationCheckRequestDto checkRequest = new VerificationCheckRequestDto(transactionId, inputCode);
    VerificationCheckResponseDto checkResponse = hwanbeeAccountClient.verifyInputCode(checkRequest);

    VerificationAccountDataDto data = checkResponse.data();
    if (data == null || !Boolean.TRUE.equals(data.verified())) {
      throw new BaseException(WalletErrorCode.FAIL_VERIFICATION);
    }

    // 3. User와 Currency 객체 저장
    User user = userService.getByIdOrThrow(userId);
    Currency currency = currencyService.getByCodeOrThrow(data.currencyCode());

    // 4. 환비 계좌 정보 저장하기
    HwanbeeAccountLink accountLink = hwanbeeLinkRepository
      .findByUserIdAndHwanbeeBankAccountNumber(userId, data.accountNumber())
      .orElseGet(() -> {
        HwanbeeAccountLink link = HwanbeeAccountLink.create(
          userId,
          data.accountNumber(),
          data.currencyCode()
        );

        return hwanbeeLinkRepository.save(link);
      });

    // 4. 지갑 번호 생성
    String walletNumber = generateWalletNumber(userId, data.currencyCode());

    // 5. 중복 지갑 체크
    if (walletRepository.existsByUserIdAndCurrencyCode(userId, data.currencyCode())) {
      throw new BaseException(WalletErrorCode.DUPLICATE_WALLET);
    }

    // 6. Wallet 생성 및 저장
    Wallet wallet = Wallet.create(
      user, // User 엔티티
      walletNumber, // 지갑 번호 (내부 규칙 생성)
      currency,   // Currency 엔티티
      accountLink // HwanbeeAccountLink 엔티티
    );
    walletRepository.save(wallet);

    return CreateWalletResponseDto.from(data.accountNumber());
  }

  @Override
  public SearchWalletResponseDto getWalletByUserAndCurrency(SearchWalletRequestDto req) {
    Long userId = userService.getCurrentUserId();

    Wallet wallet = walletRepository.findByUserIdAndCurrencyCode(userId, req.currencyCode())
      .orElseThrow(() -> BaseException.type(WalletErrorCode.NOT_FOUND_WALLET));

    return SearchWalletResponseDto.from(wallet);
  }

  // 사용자별 모든 지갑 목록 조회
  @Override
  @Transactional
  public List<SearchWalletResponseDto> getAllWalletsByUser() {
    Long userId = userService.getCurrentUserId();

    return walletRepository.findAllByUser(userId).stream()
      .map(SearchWalletResponseDto::from)
      .collect(Collectors.toList());
  }

  @Override
  public GetWalletInfoResponseDto getReceiverWalletInfo(String walletNumber) {
    Wallet wallet = walletRepository.findByWalletNumber(walletNumber)
      .orElseThrow(() -> BaseException.type(WalletErrorCode.NOT_FOUND_WALLET));

    return GetWalletInfoResponseDto.from(wallet);
  }

  @Override
  public Wallet getWalletByNumberOrThrow(String walletNumber) {
    return walletRepository.findByWalletNumber(walletNumber)
      .orElseThrow(() -> BaseException.type(WalletErrorCode.NOT_FOUND_WALLET));
  }

  // 지갑 번호 생성
  private String generateWalletNumber(Long userId, String currencyCode) {
    int random = (int)(Math.random() * 90_000_000) + 10_000_000;
    return String.format("%s-%d-%08d", currencyCode, userId, random);
  }
}
