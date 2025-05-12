package com.hbbhbank.moamoa.wallet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hbbhbank.moamoa.external.domain.UserAccountLink;
import com.hbbhbank.moamoa.external.dto.request.VerificationCheckRequestDto;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

  @Mock WalletRepository walletRepository;
  @Mock UserRepository userRepository;
  @Mock CurrencyRepository currencyRepository;
  @Mock HwanbeeAccountService hwanbeeAccountService;

  @InjectMocks WalletService walletService;

  private final Long userId = 1L;
  private final String currencyCode = "USD";
  private final String externalAccountNumber = "HWB1234567890";
  private final String verificationCode = "FX2031";

  private final CreateWalletRequestDto req =
    new CreateWalletRequestDto(currencyCode, externalAccountNumber, verificationCode);

  @Test
  void createWallet_Success() {

    User mockUser = User.builder().build();
    ReflectionTestUtils.setField(mockUser, "id", userId);

    Currency mockCurrency = Currency.builder()
      .code(currencyCode)
      .name("US Dollar")
      .isForeign(true)
      .defaultAutoChargeUnit(BigDecimal.TEN)
      .build();

    UserAccountLink mockLink = mock(UserAccountLink.class);

    Wallet savedWallet = Wallet.builder()
      .user(mockUser)
      .currency(mockCurrency)
      .accountNumber("USD-1-12345678")
      .accountLink(mockLink)
      .balance(BigDecimal.ZERO)
      .build();
    ReflectionTestUtils.setField(savedWallet, "id", 10L);

    try (MockedStatic<SecurityUtil> security = mockStatic(SecurityUtil.class)) {
      security.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

      when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
      when(currencyRepository.findByCode(currencyCode)).thenReturn(Optional.of(mockCurrency));
      when(walletRepository.existsByUserIdAndCurrencyCode(userId, currencyCode)).thenReturn(false);
      when(hwanbeeAccountService.verifyAndLinkAccount(any())).thenReturn(mockLink);
      when(walletRepository.save(any())).thenReturn(savedWallet);

      WalletResponseDto result = walletService.createWallet(req);

      assertEquals(10L, result.id());
      assertEquals(userId, result.userId());
      assertEquals("USD-1-12345678", result.accountNumber());
      assertEquals(currencyCode, result.currencyCode());
      assertEquals("US Dollar", result.currencyName());
    }
  }


  @Test
  void createWallet_UserNotFound() {
    try (MockedStatic<SecurityUtil> security = mockStatic(SecurityUtil.class)) {
      security.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      BaseException ex = assertThrows(BaseException.class, () -> walletService.createWallet(req));
      assertEquals(UserErrorCode.USER_NOT_FOUND, ex.getCode());
    }
  }

  @Test
  void createWallet_CurrencyNotFound() {
    User mockUser = User.builder().build();

    try (MockedStatic<SecurityUtil> security = mockStatic(SecurityUtil.class)) {
      security.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
      when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
      when(currencyRepository.findByCode(currencyCode)).thenReturn(Optional.empty());

      BaseException ex = assertThrows(BaseException.class, () -> walletService.createWallet(req));
      assertEquals(WalletErrorCode.CURRENCY_CODE_NOT_FOUND, ex.getCode());
    }
  }

  @Test
  void createWallet_DuplicateWallet() {
    User mockUser = User.builder().build();
    Currency mockCurrency = Currency.builder()
      .code(currencyCode)
      .name("USD")
      .isForeign(true)
      .defaultAutoChargeUnit(BigDecimal.TEN)
      .build();

    try (MockedStatic<SecurityUtil> security = mockStatic(SecurityUtil.class)) {
      security.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
      when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
      when(currencyRepository.findByCode(currencyCode)).thenReturn(Optional.of(mockCurrency));
      when(walletRepository.existsByUserIdAndCurrencyCode(userId, currencyCode)).thenReturn(true);

      BaseException ex = assertThrows(BaseException.class, () -> walletService.createWallet(req));
      assertEquals(WalletErrorCode.DUPLICATE_WALLET, ex.getCode());
    }
  }
}
