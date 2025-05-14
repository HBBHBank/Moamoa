package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.user.service.UserService;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;
import com.hbbhbank.moamoa.wallet.dto.request.CreateTransactionRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.CreateWalletTransactionResponseDto;
import com.hbbhbank.moamoa.wallet.exception.WalletErrorCode;
import com.hbbhbank.moamoa.wallet.repository.WalletRepository;
import com.hbbhbank.moamoa.wallet.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {

  private final WalletTransactionRepository walletTransactionRepository;
  private final WalletRepository walletRepository;
  private final UserService userService;

  @Override
  public WalletTransaction showWalletTransaction(String currencyCode) {
    return walletTransactionRepository.findByCurrencyCode(currencyCode)
      .orElseThrow(() -> BaseException.type(WalletErrorCode.NOT_FOUND_WALLET));
  }

  @Override
  public List<WalletTransaction> getAllTransactionsByWallet(String currencyCode) {
    Long userId = userService.getCurrentUserId();
    return walletTransactionRepository.findListByUserAndCurrency(userId, currencyCode);
  }

  @Override
  public List<WalletTransaction> getTransactionsByWalletAndType(String currencyCode, WalletTransactionType type) {
    Long userId = userService.getCurrentUserId();
    return walletTransactionRepository.findListByUserAndCurrencyAndType(userId, currencyCode, type);
  }

  @Override
  @Transactional
  public CreateWalletTransactionResponseDto recordTransaction(CreateTransactionRequestDto req) {
    // 1. 지갑 조회
    Wallet wallet = walletRepository.findByIdOrThrow(req.walletId());

    // 2. 상대 지갑
    Wallet counter = req.counterWalletId() != null
      ? walletRepository.findByIdOrThrow(req.counterWalletId())
      : null;

    // 3. 입/출금 처리
    applyBalanceChange(wallet, req);

    // 4. 도메인 객체 생성
    WalletTransaction tx = WalletTransaction.create(
      wallet,
      counter,
      req.type(),
      req.amount(),
      req.includedInSettlement()
    );

    // 5. 저장 및 반환
    walletTransactionRepository.save(tx);
    return CreateWalletTransactionResponseDto.from(tx);
  }

  // 거래 타입(ex. 환전, 송금 등)에 따라 입금인지 출금인지 판단하고 잔액을 변경
  private void applyBalanceChange(Wallet wallet, CreateTransactionRequestDto req) {
    if (req.type().isIncomeType()) {
      wallet.increaseBalance(req.amount());
    } else if (req.type().isExpenseType()) {
      wallet.decreaseBalance(req.amount());
    } else {
      throw BaseException.type(WalletErrorCode.INVALID_TRANSACTION_TYPE);
    }
  }

}