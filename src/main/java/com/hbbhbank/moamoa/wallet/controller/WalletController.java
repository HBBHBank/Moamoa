package com.hbbhbank.moamoa.wallet.controller;

import com.hbbhbank.moamoa.wallet.dto.request.CreateWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.WalletInquiryRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.WalletInquiryResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.WalletResponseDto;
import com.hbbhbank.moamoa.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

  private final WalletService walletService;

  /**
   * 지갑 생성
   */
  @PostMapping
  public ResponseEntity<WalletResponseDto> createWallet(
    @Valid @RequestBody CreateWalletRequestDto req
  ) {
    WalletResponseDto response = walletService.createWallet(req);
    return ResponseEntity.ok(response);
  }

  /**
   * 사용자의 전체 지갑 목록 조회
   */
  @GetMapping
  public ResponseEntity<List<WalletInquiryResponseDto>> getAllWalletsByUser() {
    List<WalletInquiryResponseDto> response = walletService.getAllWalletsByUser();
    return ResponseEntity.ok(response);
  }

  /**
   * 특정 통화의 지갑 정보 조회
   */
  @GetMapping("/currency")
  public ResponseEntity<WalletInquiryResponseDto> showWalletByCurrency(
    @RequestParam String currencyCode
  ) {
    WalletInquiryRequestDto req = new WalletInquiryRequestDto(currencyCode);
    WalletInquiryResponseDto response = walletService.showWallet(req);
    return ResponseEntity.ok(response);
  }

}

