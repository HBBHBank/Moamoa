//package com.hbbhbank.moamoa.transfer.service;
//
//import com.hbbhbank.moamoa.global.exception.BaseException;
//import com.hbbhbank.moamoa.transfer.domain.Transfer;
//import com.hbbhbank.moamoa.transfer.domain.TransferStatus;
//import com.hbbhbank.moamoa.transfer.dto.request.PointTransferRequestDto;
//import com.hbbhbank.moamoa.transfer.dto.response.PointTransferResponseDto;
//import com.hbbhbank.moamoa.transfer.exception.TransferErrorCode;
//import com.hbbhbank.moamoa.transfer.repository.PointTransferRepository;
//import com.hbbhbank.moamoa.wallet.domain.Wallet;
//import com.hbbhbank.moamoa.wallet.service.WalletService;
//import com.hbbhbank.moamoa.wallet.service.WalletTransactionService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//
//import static com.hbbhbank.moamoa.wallet.domain.WalletTransactionType.TRANSFER_IN;
//import static com.hbbhbank.moamoa.wallet.domain.WalletTransactionType.TRANSFER_OUT;
//
//@Service
//@RequiredArgsConstructor
//public class PointTransferServiceImpl implements PointTransferService {
//
//  private final WalletService walletService;
//  private final PointTransferRepository pointTransferRepository;
//  private final WalletTransactionService walletTransactionService;
//
//  // 직접 송금 요청
//  @Override
//  @Transactional
//  public PointTransferResponseDto transferByUser(PointTransferRequestDto req) {
//    Wallet from = walletService.getWalletByNumberOrThrow(req.fromWalletNumber());
//    Wallet to = walletService.getWalletByNumberOrThrow(req.toWalletNumber());
//
//    validate(from, to, req.amount());
//
//    // 거래 내역은 여기에서만 기록
//    walletTransactionService.recordTransaction(from.getId(), to.getId(), TRANSFER_OUT, req.amount(), false);
//    walletTransactionService.recordTransaction(to.getId(), from.getId(), TRANSFER_IN, req.amount(), false);
//
//    // 도메인 저장만 공통 처리
//    Transfer transfer = executeTransfer(from, to, req.amount());
//
//    return PointTransferResponseDto.from(transfer);
//  }
//
//  private Transfer executeTransfer(Wallet from, Wallet to, BigDecimal amount) {
//    Transfer transfer = Transfer.create(from, to, amount, TransferStatus.SUCCESS);
//    return pointTransferRepository.save(transfer);
//  }
//
//  // 유효성 검증 메서드
//  private void validate(Wallet from, Wallet to, BigDecimal amount) {
//    if (!from.getCurrency().equals(to.getCurrency())) {
//      throw BaseException.type(TransferErrorCode.CURRENCY_MISMATCH);
//    }
//    if (from.getBalance().compareTo(amount) < 0) {
//      throw BaseException.type(TransferErrorCode.INSUFFICIENT_BALANCE);
//    }
//  }
//}