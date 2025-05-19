package com.hbbhbank.moamoa.recharge.service;

import com.hbbhbank.moamoa.external.dto.request.transfer.TransferRequestDto;
import com.hbbhbank.moamoa.external.dto.response.transfer.TransferResponseDto;
import com.hbbhbank.moamoa.external.service.HwanbeeExchangeService;
import com.hbbhbank.moamoa.external.service.HwanbeeTransferService;
import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.global.security.util.SecurityUtil;
import com.hbbhbank.moamoa.recharge.domain.Recharge;
import com.hbbhbank.moamoa.recharge.domain.RechargeMethod;
import com.hbbhbank.moamoa.recharge.dto.request.RechargeRequestDto;
import com.hbbhbank.moamoa.recharge.dto.response.RechargeResponseDto;
import com.hbbhbank.moamoa.recharge.exception.RechargeErrorCode;
import com.hbbhbank.moamoa.recharge.repository.RechargeRepository;
import com.hbbhbank.moamoa.wallet.domain.Currency;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.exception.WalletErrorCode;
import com.hbbhbank.moamoa.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.hbbhbank.moamoa.global.constant.Constants.MOAMOA_ACCOUNT;

@Service
@RequiredArgsConstructor
public class RechargeService {

  private final RechargeRepository rechargeRepository;
  private final WalletRepository walletRepository;
  private final HwanbeeTransferService hwanbeeTransferService;
  private final HwanbeeExchangeService hwanbeeExchangeService;

  @Transactional
  public RechargeResponseDto directCharge(RechargeRequestDto req) {
    Long userId = SecurityUtil.getCurrentUserId(); // 로그인한 사용자 ID

    // 직접 충전할 지갑 조회
    Wallet found = walletRepository.findByUserIdAndCurrencyCode(userId, req.currencyCode())
      .orElseThrow(() -> BaseException.type(WalletErrorCode.NOT_FOUND_WALLET));

    Currency currency = found.getCurrency(); // 해당 지갑의 통화 코드 조회

    // 외화는 직접 충전 불가능. 무조건 환전 이용.
    if (currency.isForeign()) {
      throw BaseException.type(RechargeErrorCode.DIRECT_RECHARGE_NOT_ALLOWED_FOR_FOREIGN_CURRENCY);
    }

    BigDecimal defaultAmount = BigDecimal.valueOf(10000); // 기본 충전 금액 10,000

    // 만약 만원 단위가 아니라면, 예외 발생
    if (req.amount().remainder(defaultAmount).compareTo(BigDecimal.ZERO) != 0) {
      throw BaseException.type(RechargeErrorCode.INVALID_AMOUNT);
    }

    // 실제 송금 실행 및 검증
    TransferResponseDto transferResult = hwanbeeTransferService.transfer(
      new TransferRequestDto(
        userId,
        found.getAccountLink().getExternalBankAccountNumber(), // 외부 계좌 번호
        MOAMOA_ACCOUNT, // 모아모아 계좌 번호
        req.amount(),
        req.currencyCode(),
        LocalDateTime.now()
      )
    );

    found.increaseBalance(req.amount()); // 충전된 금액만큼 잔액 업데이트

    // 충전 기록을 DB에 저장
    Recharge recharge = rechargeRepository.save(
      Recharge.builder()
        .wallet(found)
        .amount(req.amount())
        .method(RechargeMethod.AUTO)
        .accountLink(found.getAccountLink())
        .rechargedAt(LocalDateTime.now())
        .build()
    );

    return RechargeResponseDto.from(recharge);
  }
}
