package com.hbbhbank.moamoa.external.service;

import com.hbbhbank.moamoa.external.client.HwanbeeTransferClientImpl;
import com.hbbhbank.moamoa.external.domain.UserAccountLink;
import com.hbbhbank.moamoa.external.dto.request.transfer.TransferRequestDto;
import com.hbbhbank.moamoa.external.dto.response.transfer.TransferResponseDto;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class HwanbeeTransferService {

  private final HwanbeeTransferClientImpl hwanbeeTransferClient;

  public TransferResponseDto transfer(TransferRequestDto dto) {
    return hwanbeeTransferClient.requestTransfer(dto);
  }

  public TransferResponseDto transferFromWalletToLinkedAccount(Wallet fromWallet, UserAccountLink toAccount, BigDecimal amount) {
    // 1. 송금 요청 DTO를 생성
    TransferRequestDto dto = TransferRequestDto.of(
      fromWallet.getUser().getId(),
      fromWallet.getWalletNumber(),
      toAccount.getExternalBankAccountNumber(),
      amount,
      fromWallet.getCurrency().getCode()
    );

    // 2. 외부 API 호출
    return transfer(dto);
  }
}