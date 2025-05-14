package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;
import com.hbbhbank.moamoa.wallet.dto.request.CreateWalletTransactionRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.WalletInquiryRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.CreateWalletTransactionResponseDto;
import com.hbbhbank.moamoa.wallet.exception.WalletErrorCode;
import com.hbbhbank.moamoa.wallet.repository.WalletRepository;
import com.hbbhbank.moamoa.wallet.repository.WalletTransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class WalletTransactionServiceTest {

  @InjectMocks
  private WalletTransactionServiceImpl walletTransactionService;

  @Mock
  private WalletTransactionRepository walletTransactionRepository;

  @Mock
  private WalletRepository walletRepository;

  @Test
  @DisplayName("거래 내역 생성 성공")
  void should_record_transaction_successfully() {
    // given
    Long walletId = 1L;
    CreateWalletTransactionRequestDto req = new CreateWalletTransactionRequestDto(
      walletId,
      null,
      WalletTransactionType.WITHDRAWAL,
      BigDecimal.valueOf(1000),
      false
    );

    Wallet mockWallet = Wallet.builder().balance(BigDecimal.ZERO).build();
    ReflectionTestUtils.setField(mockWallet, "id", walletId);

    given(walletRepository.getReferenceById(walletId)).willReturn(mockWallet);
    given(walletTransactionRepository.save(any(WalletTransaction.class)))
      .willAnswer(invocation -> invocation.getArgument(0));

    // when
    CreateWalletTransactionResponseDto response = walletTransactionService.recordTransaction(req);

    // then
    assertThat(response).isNotNull();
    assertThat(response.amount()).isEqualTo(BigDecimal.valueOf(1000));
    assertThat(response.type()).isEqualTo(WalletTransactionType.WITHDRAWAL);
  }

  @Test
  @DisplayName("거래 내역 조회 실패: 지갑 없음")
  void should_throw_when_wallet_not_found() {
    // given
    WalletInquiryRequestDto req = new WalletInquiryRequestDto("USD");
    given(walletTransactionRepository.findByWallet_Currency_Code("USD"))
      .willReturn(Optional.empty());

    // when
    BaseException exception = assertThrows(BaseException.class, () ->
      walletTransactionService.showWalletTransaction(req));

    // then
    assertThat(exception.getCode()).isEqualTo(WalletErrorCode.NOT_FOUND_WALLET);
  }

  @Test
  @DisplayName("거래 내역 조회 성공")
  void should_show_transaction_by_currency_code() {
    // given
    WalletTransaction tx = WalletTransaction.builder()
      .amount(BigDecimal.valueOf(5000))
      .type(WalletTransactionType.WITHDRAWAL)
      .transactedAt(LocalDateTime.now())
      .build();
    WalletInquiryRequestDto req = new WalletInquiryRequestDto("USD");

    given(walletTransactionRepository.findByWallet_Currency_Code("USD"))
      .willReturn(Optional.of(tx));

    // when
    WalletTransaction result = walletTransactionService.showWalletTransaction(req);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(5000));
    assertThat(result.getType()).isEqualTo(WalletTransactionType.WITHDRAWAL);
  }
}
