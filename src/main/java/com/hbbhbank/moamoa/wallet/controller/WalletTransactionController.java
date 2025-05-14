package com.hbbhbank.moamoa.wallet.controller;

import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;
import com.hbbhbank.moamoa.wallet.dto.request.WalletInquiryRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.CreateWalletTransactionResponseDto;
import com.hbbhbank.moamoa.wallet.service.WalletTransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallet/transactions")
@RequiredArgsConstructor
@Validated
public class WalletTransactionController {

  private final WalletTransactionServiceImpl walletTransactionService;

  @GetMapping
  public ResponseEntity<CreateWalletTransactionResponseDto> showWalletTransaction(
    @RequestParam("currencyCode") String currencyCode
  ) {
    WalletInquiryRequestDto req = new WalletInquiryRequestDto(currencyCode);
    WalletTransaction transaction = walletTransactionService.showWalletTransaction(req);
    return ResponseEntity.ok(CreateWalletTransactionResponseDto.from(transaction));
  }
}

