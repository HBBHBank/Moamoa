package com.hbbhbank.moamoa.wallet.controller;

import com.hbbhbank.moamoa.external.dto.response.GetVerificationCodeResponseDto;
import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.wallet.dto.request.CreateWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.GetVerificationCodeWithinMoamoaRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.WalletInquiryRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.WalletInquiryResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.CreateWalletResponseDto;
import com.hbbhbank.moamoa.wallet.service.WalletServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@Validated
public class WalletController {

  private final WalletServiceImpl walletService;

  // 가상 지갑 생성 전 외부 실계좌 인증코드 발급 요청, 환비 계좌 API를 통해 1회성 입금 인증코드를 발급
  @PostMapping("/verification-code")
  public ResponseEntity<BaseResponse<GetVerificationCodeResponseDto>> generateVerificationCode(
    @RequestBody @Valid GetVerificationCodeWithinMoamoaRequestDto requestDto
  ) {
    return ResponseEntity.ok(BaseResponse.success(
      walletService.getVerificationCode(requestDto))
    );
  }

  // 외부 계좌 인증 확인 후 사용자 가상 지갑 생성. 사용자가 인증코드로 입금한 후, 이를 검증하여 가상 지갑을 생성
  @PostMapping
  public ResponseEntity<BaseResponse<CreateWalletResponseDto>> createWallet(
    @RequestBody @Valid CreateWalletRequestDto requestDto
  ) {
    return ResponseEntity.ok(BaseResponse.success(
      walletService.createWalletAfterVerification(requestDto))
    );
  }

  // 특정 통화의 사용자 가상 지갑 정보 조회
  @GetMapping
  public ResponseEntity<BaseResponse<WalletInquiryResponseDto>> showWallet(
    @RequestParam String currencyCode
  ) {
    WalletInquiryRequestDto dto = new WalletInquiryRequestDto(currencyCode);
    return ResponseEntity.ok(BaseResponse.success(
      walletService.getWalletByUserAndCurrency(dto))
    );
  }

  // 사용자의 모든 통화별 가상 지갑 목록 조회
  @GetMapping("/all")
  public ResponseEntity<BaseResponse<List<WalletInquiryResponseDto>>> getAllWallets() {
    return ResponseEntity.ok(BaseResponse.success(
      walletService.getAllWalletsByUser())
    );
  }

}

