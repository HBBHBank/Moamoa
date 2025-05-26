package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.external.auth.OAuth2TokenService;
import com.hbbhbank.moamoa.external.client.HwanbeeAccountClient;
import com.hbbhbank.moamoa.external.dto.request.account.VerificationCheckRequestDto;
import com.hbbhbank.moamoa.external.dto.request.account.VerificationCodeRequestDto;
import com.hbbhbank.moamoa.external.dto.response.account.VerificationAccountDataDto;
import com.hbbhbank.moamoa.external.dto.response.account.VerificationCheckResponseDto;
import com.hbbhbank.moamoa.external.dto.response.account.VerificationCodeResponseDto;
import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.user.exception.UserErrorCode;
import com.hbbhbank.moamoa.user.repository.UserRepository;
import com.hbbhbank.moamoa.user.service.UserService;
import com.hbbhbank.moamoa.wallet.domain.AccountVerificationRequest;
import com.hbbhbank.moamoa.wallet.domain.Currency;
import com.hbbhbank.moamoa.wallet.domain.HwanbeeAccountLink;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.dto.request.wallet.SearchWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.wallet.CreateWalletResponseDto;
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
  private final UserRepository userRepository;
  private final OAuth2TokenService oAuth2TokenClient;

  /**
   * 환비에 인증코드 발급 요청
   */
  @Override
  public void requestVerificationCode(VerificationCodeRequestDto req, String authorizationCode) {
    Long userId = userService.getCurrentUserId();
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));

    // access token이 없거나 만료되었으면 발급
    String accessToken = oAuth2TokenClient.ensureAccessToken(user, authorizationCode);

    // 환비 API 호출 - 1원 송금 요청
    VerificationCodeResponseDto response = hwanbeeAccountClient.requestVerificationCode(req, accessToken);

    String transactionId = response.data() != null ? response.data().transactionId() : null;
    if (transactionId == null) {
      throw new BaseException(WalletErrorCode.FAIL_VERIFICATION);
    }

    // 추후 확인용으로 인증 요청 저장
    accountVerificationRequestRepository.save(AccountVerificationRequest.from(transactionId));
  }

  /**
   * 환비 계좌 인증이 완료된 후 지갑 생성
   */
  @Override
  @Transactional
  public CreateWalletResponseDto createWalletAfterVerification(Integer inputCode) {
    Long userId = userService.getCurrentUserId();

    // 가장 최근의 인증 요청(transactionId) 가져오기
    AccountVerificationRequest verificationRequest = accountVerificationRequestRepository
      .findByUserId(userId)
      .orElseThrow(() -> new BaseException(WalletErrorCode.FAIL_VERIFICATION));
    String transactionId = verificationRequest.getTransactionId();

    // 입력 코드로 인증 확인
    VerificationCheckRequestDto checkRequest = new VerificationCheckRequestDto(transactionId, inputCode);
    VerificationCheckResponseDto checkResponse = hwanbeeAccountClient.verifyInputCode(checkRequest);

    VerificationAccountDataDto data = checkResponse.data();
    if (data == null || !Boolean.TRUE.equals(data.verified())) {
      throw new BaseException(WalletErrorCode.FAIL_VERIFICATION);
    }

    // 통화 및 사용자 정보 조회
    User user = userService.getByIdOrThrow(userId);
    Currency currency = currencyService.getByCodeOrThrow(data.currencyCode());

    // 환비 계좌 저장 (중복 체크)
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

    // 중복 지갑 확인
    if (walletRepository.existsByUserIdAndCurrencyCode(userId, data.currencyCode())) {
      throw new BaseException(WalletErrorCode.DUPLICATE_WALLET);
    }

    // 지갑 번호 생성 및 지갑 생성
    String walletNumber = generateWalletNumber(userId, data.currencyCode());
    Wallet wallet = Wallet.create(user, walletNumber, currency, accountLink);
    walletRepository.save(wallet);

    return CreateWalletResponseDto.from(data.accountNumber());
  }


  /**
   * 사용자별 통화 코드로 지갑 조회
   */
  @Override
  public SearchWalletResponseDto getWalletByUserAndCurrency(SearchWalletRequestDto req) {
    Long userId = userService.getCurrentUserId();

    Wallet wallet = walletRepository.findByUserIdAndCurrencyCode(userId, req.currencyCode())
      .orElseThrow(() -> BaseException.type(WalletErrorCode.NOT_FOUND_WALLET));

    return SearchWalletResponseDto.from(wallet);
  }

  /**
   * 사용자별 모든 지갑 목록 조회
   */
  @Override
  @Transactional
  public List<SearchWalletResponseDto> getAllWalletsByUser() {
    Long userId = userService.getCurrentUserId();

    return walletRepository.findAllByUser(userId).stream()
      .map(SearchWalletResponseDto::from)
      .collect(Collectors.toList());
  }

  /**
   * 지갑 번호로 지갑 조회
   */
  @Override
  public Wallet getWalletByNumberOrThrow(String walletNumber) {
    return walletRepository.findByWalletNumber(walletNumber)
      .orElseThrow(() -> BaseException.type(WalletErrorCode.NOT_FOUND_WALLET));
  }

  /**
   * 지갑 번호 생성
   */
  private String generateWalletNumber(Long userId, String currencyCode) {
    int random = (int)(Math.random() * 90_000_000) + 10_000_000;
    return String.format("%s-%d-%08d", currencyCode, userId, random);
  }
}
