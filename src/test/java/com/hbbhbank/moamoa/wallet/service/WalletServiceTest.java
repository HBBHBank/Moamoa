package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.external.domain.UserAccountLink;
import com.hbbhbank.moamoa.external.exception.HwanbeeErrorCode;
import com.hbbhbank.moamoa.external.service.HwanbeeAccountService;
import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.global.security.util.SecurityUtil;
import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.user.exception.UserErrorCode;
import com.hbbhbank.moamoa.user.repository.UserRepository;
import com.hbbhbank.moamoa.wallet.domain.Currency;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.dto.request.CreateWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.WalletResponseDto;
import com.hbbhbank.moamoa.wallet.exception.WalletErrorCode;
import com.hbbhbank.moamoa.wallet.repository.CurrencyRepository;
import com.hbbhbank.moamoa.wallet.repository.WalletRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

  @InjectMocks
  private WalletService walletService;

  @Mock
  private WalletRepository walletRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CurrencyRepository currencyRepository;

  @Mock
  private HwanbeeAccountService hwanbeeAccountService;

  private MockedStatic<SecurityUtil> mockStatic;

  @BeforeEach
  void setUp() {
    mockStatic = Mockito.mockStatic(SecurityUtil.class);
    mockStatic.when(SecurityUtil::getCurrentUserId).thenReturn(1L);
  }

  @AfterEach
  void tearDown() {
    mockStatic.close();
  }

  @Test
  @DisplayName("지갑 생성 성공")
  void should_create_wallet_successfully() {
    // given
    Long userId = 1L;
    User user = User.builder().build();
    Currency currency = new Currency("USD", "미국 달러", true, BigDecimal.valueOf(10));
    CreateWalletRequestDto req = new CreateWalletRequestDto("USD", "HWB1234567890", "FX1234");

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(currencyRepository.findByCode("USD")).willReturn(Optional.of(currency));
    given(walletRepository.existsByUserIdAndCurrencyCode(userId, "USD")).willReturn(false);
    given(hwanbeeAccountService.verifyAndLinkAccount(user, "HWB1234567890", "FX1234"))
      .willReturn(UserAccountLink.builder().user(user).externalBankAccountNumber("HWB1234567890").build());
    given(walletRepository.save(any(Wallet.class))).willAnswer(invocation -> invocation.getArgument(0));

    // when
    WalletResponseDto response = walletService.createWallet(req);

    // then
    assertThat(response).isNotNull();
    assertThat(response.currencyCode()).isEqualTo("USD");
    assertThat(response.balance()).isEqualTo(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("지갑 생성 실패 - 존재하지 않는 사용자")
  void should_throw_when_user_not_found() {
    // given
    given(userRepository.findById(1L)).willReturn(Optional.empty());

    // when
    BaseException exception = assertThrows(BaseException.class, () ->
      walletService.createWallet(new CreateWalletRequestDto("USD", "HWB1234567890", "FX1234")));

    // then
    assertThat(exception.getCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
  }

  @Test
  @DisplayName("지갑 생성 실패 - 존재하지 않는 통화")
  void should_throw_when_currency_not_found() {
    // given
    User user = User.builder().build();
    given(userRepository.findById(1L)).willReturn(Optional.of(user));
    given(currencyRepository.findByCode("USD")).willReturn(Optional.empty());

    // when
    BaseException exception = assertThrows(BaseException.class, () ->
      walletService.createWallet(new CreateWalletRequestDto("USD", "HWB1234567890", "FX1234")));

    // then
    assertThat(exception.getCode()).isEqualTo(WalletErrorCode.CURRENCY_CODE_NOT_FOUND);
  }

  @Test
  @DisplayName("지갑 생성 실패 - 중복 지갑")
  void should_throw_when_wallet_already_exists() {
    // given
    User user = User.builder().build();
    Currency currency = new Currency("USD", "미국 달러", true, BigDecimal.valueOf(10));

    given(userRepository.findById(1L)).willReturn(Optional.of(user));
    given(currencyRepository.findByCode("USD")).willReturn(Optional.of(currency));
    given(walletRepository.existsByUserIdAndCurrencyCode(1L, "USD")).willReturn(true);

    // when
    BaseException exception = assertThrows(BaseException.class, () ->
      walletService.createWallet(new CreateWalletRequestDto("USD", "HWB1234567890", "FX1234")));

    // then
    assertThat(exception.getCode()).isEqualTo(WalletErrorCode.DUPLICATE_WALLET);
  }

  @Test
  @DisplayName("지갑 생성 실패 - 외부 계좌 인증 실패")
  void should_throw_when_account_verification_fails() {
    // given
    User user = User.builder().build();
    Currency currency = new Currency("USD", "미국 달러", true, BigDecimal.valueOf(10));

    given(userRepository.findById(1L)).willReturn(Optional.of(user));
    given(currencyRepository.findByCode("USD")).willReturn(Optional.of(currency));
    given(walletRepository.existsByUserIdAndCurrencyCode(1L, "USD")).willReturn(false);
    given(hwanbeeAccountService.verifyAndLinkAccount(user, "HWB1234567890", "FX1234"))
      .willThrow(BaseException.type(HwanbeeErrorCode.ACCOUNT_VERIFICATION_FAILED));

    // when
    BaseException exception = assertThrows(BaseException.class, () ->
      walletService.createWallet(new CreateWalletRequestDto("USD", "HWB1234567890", "FX1234")));

    // then
    assertThat(exception.getCode()).isEqualTo(HwanbeeErrorCode.ACCOUNT_VERIFICATION_FAILED);
  }
}