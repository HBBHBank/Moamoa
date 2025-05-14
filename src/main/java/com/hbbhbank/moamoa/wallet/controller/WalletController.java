package com.hbbhbank.moamoa.wallet.controller;

import com.hbbhbank.moamoa.external.dto.response.account.GetVerificationCodeResponseDto;
import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.wallet.dto.request.CreateWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.GetVerificationCodeWithinMoamoaRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.SearchWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.CreateWalletResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.GetWalletInfoResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.SearchWalletResponseDto;
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

  /**
   * 외부 실계좌 인증을 위한 1회용 인증코드를 요청합니다.
   * 인증 코드는 환비 API를 통해 발급되며, 사용자는 이 코드를 이용해 인증을 완료해야 합니다.
   *
   * @param requestDto 외부 계좌번호, 통화코드 등을 포함한 요청 DTO
   * @return 인증코드
   */
  @PostMapping("/verification-code")
  public ResponseEntity<BaseResponse<GetVerificationCodeResponseDto>> generateVerificationCode(
    @RequestBody @Valid GetVerificationCodeWithinMoamoaRequestDto requestDto
  ) {
    return ResponseEntity.ok(BaseResponse.success(
      walletService.getVerificationCode(requestDto))
    );
  }

  /**
   * 외부 계좌 인증을 마친 후, 사용자 가상 지갑을 생성합니다.
   * 지갑은 통화 코드 및 외부 계좌 정보에 따라 하나만 생성 가능하며, 중복 생성은 불가능합니다.
   *
   * @param requestDto 통화코드, 외부 계좌번호, 인증코드 등의 정보를 담은 요청 DTO
   * @return 지갑 id, 사용자 이름, 지갑 번호, 통화 코드, 통화 이름, 잔액, 환비 계좌번호
   */
  @PostMapping
  public ResponseEntity<BaseResponse<CreateWalletResponseDto>> createWallet(
    @RequestBody @Valid CreateWalletRequestDto requestDto
  ) {
    return ResponseEntity.ok(BaseResponse.success(
      walletService.createWalletAfterVerification(requestDto))
    );
  }

  /**
   * 현재 로그인한 사용자의 특정 통화에 해당하는 지갑 정보를 조회합니다.
   *
   * @param currencyCode 통화 코드 (예: KRW, USD)
   * @return 지갑 번호, 통화 코드, 통화 이름, 잔액, 환비 계좌번호
   */
  @GetMapping
  public ResponseEntity<BaseResponse<SearchWalletResponseDto>> showWallet(
    @RequestParam String currencyCode
  ) {
    SearchWalletRequestDto dto = new SearchWalletRequestDto(currencyCode);
    return ResponseEntity.ok(BaseResponse.success(
      walletService.getWalletByUserAndCurrency(dto))
    );
  }

  /**
   * 현재 로그인한 사용자의 모든 가상 지갑 목록을 조회합니다.
   *
   * @return 통화별 지갑 정보 리스트
   */
  @GetMapping("/all")
  public ResponseEntity<BaseResponse<List<SearchWalletResponseDto>>> getAllWallets() {
    return ResponseEntity.ok(BaseResponse.success(
      walletService.getAllWalletsByUser())
    );
  }

  /**
   * 송금 전 받는 사람의 지갑 번호를 통해 사용자 일부 실명과 통화 정보를 조회합니다.
   * 프론트엔드에서는 이를 통해 사용자에게 송금 대상 지갑이 정확한지 시각적으로 확인시킬 수 있습니다.
   *
   * @param walletNumber 지갑 번호
   * @return 받는 사람의 실명 일부, 지갑 번호, 통화 코드 정보
   */
  @GetMapping("/recipient")
  public ResponseEntity<BaseResponse<GetWalletInfoResponseDto>> getRecipientInfo(@RequestParam String walletNumber) {
    return ResponseEntity.ok(BaseResponse.success(walletService.getReceiverWalletInfo(walletNumber)));
  }

}

