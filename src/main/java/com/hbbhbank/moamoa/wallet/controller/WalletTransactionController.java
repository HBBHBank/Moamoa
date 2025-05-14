package com.hbbhbank.moamoa.wallet.controller;

import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;
import com.hbbhbank.moamoa.wallet.dto.request.SearchTransactionRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.SearchTransactionResponseDto;
import com.hbbhbank.moamoa.wallet.service.WalletTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet/transactions")
@RequiredArgsConstructor
public class WalletTransactionController {

  private final WalletTransactionService walletTransactionService;

  /**
   * 거래 내역 단건 조회 (가장 최근 거래)
   */
  @PostMapping("/latest")
  public ResponseEntity<BaseResponse<SearchTransactionResponseDto>> getLatestTransaction(
    @RequestBody @Valid SearchTransactionRequestDto req
  ) {
    WalletTransaction transaction = walletTransactionService.showWalletTransaction(req.currencyCode());
    return ResponseEntity.ok(BaseResponse.success(
      SearchTransactionResponseDto.from(transaction)
    ));
  }

  /**
   * 거래 내역 목록 조회 (거래 타입이 null인 경우 전체, 아니면 조건부 조회)
   */
  @PostMapping("/list")
  public ResponseEntity<BaseResponse<List<SearchTransactionResponseDto>>> getTransactionList(
    @RequestBody @Valid SearchTransactionRequestDto req
  ) {
    List<WalletTransaction> result;

    if (req.type() == null) {
      result = walletTransactionService.getAllTransactionsByWallet(req.currencyCode());
    } else {
      result = walletTransactionService.getTransactionsByWalletAndType(req.currencyCode(), req.type());
    }

    List<SearchTransactionResponseDto> responseDtos = result.stream()
      .map(SearchTransactionResponseDto::from)
      .toList();

    return ResponseEntity.ok(BaseResponse.success(responseDtos));
  }
}
