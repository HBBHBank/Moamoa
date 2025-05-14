package com.hbbhbank.moamoa.transfer.service;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.transfer.domain.Transfer;
import com.hbbhbank.moamoa.transfer.domain.TransferStatus;
import com.hbbhbank.moamoa.transfer.dto.request.PointTransferRequestDto;
import com.hbbhbank.moamoa.transfer.dto.response.PointTransferResponseDto;
import com.hbbhbank.moamoa.transfer.exception.TransferErrorCode;
import com.hbbhbank.moamoa.transfer.repository.PointTransferRepository;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;
import com.hbbhbank.moamoa.wallet.service.WalletService;
import com.hbbhbank.moamoa.wallet.service.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.hbbhbank.moamoa.wallet.domain.WalletTransactionType.TRANSFER_IN;
import static com.hbbhbank.moamoa.wallet.domain.WalletTransactionType.TRANSFER_OUT;

@Service
@RequiredArgsConstructor
public class PointTransferServiceImpl implements PointTransferService {

  private final WalletService walletService;
  private final PointTransferRepository pointTransferRepository;
  private final WalletTransactionService walletTransactionService;

  // 직접 송금 요청
  @Override
  @Transactional
  public PointTransferResponseDto transferByUser(PointTransferRequestDto req) {
    // 1. 지갑 번호로 도메인 객체 조회
    Wallet from = walletService.getWalletByNumberOrThrow(req.fromWalletNumber());
    Wallet to = walletService.getWalletByNumberOrThrow(req.toWalletNumber());

    // 2. 유효성 검증
    validate(from, to, req.amount());

    // 3. 송금 실행
    return executeTransfer(from, to, req.amount());
  }

  // 정산, 결제 등에서 이용
  @Override
  @Transactional
  public void transferInternally(Wallet from, Wallet to, BigDecimal amount) {
    validate(from, to, amount);
    executeTransfer(from, to, amount);
  }

  // 송금 실행 메서드 (항상 송금을 진행한 사람 기준)
  private PointTransferResponseDto executeTransfer(Wallet from, Wallet to, BigDecimal amount) {

    // 1. 송금 도메인 생성 및 저장
    Transfer transfer = Transfer.create(from, to, amount, TransferStatus.SUCCESS);
    pointTransferRepository.save(transfer);

    // 2. 지갑 거래 내역 기록
    walletTransactionService.recordTransaction(from.getId(), to.getId(), TRANSFER_OUT, amount, false);
    walletTransactionService.recordTransaction(to.getId(), from.getId(), TRANSFER_IN, amount, false);

    // 3. 응답 변환
    return PointTransferResponseDto.from(transfer);
  }

  // 유효성 검증 메서드
  private void validate(Wallet from, Wallet to, BigDecimal amount) {
    if (!from.getCurrency().equals(to.getCurrency())) {
      throw BaseException.type(TransferErrorCode.CURRENCY_MISMATCH);
    }
    if (from.getBalance().compareTo(amount) < 0) {
      throw BaseException.type(TransferErrorCode.INSUFFICIENT_BALANCE);
    }
  }
}
