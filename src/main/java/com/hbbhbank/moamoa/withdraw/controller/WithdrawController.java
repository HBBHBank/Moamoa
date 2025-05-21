//package com.hbbhbank.moamoa.withdraw.controller;
//
//import com.hbbhbank.moamoa.global.common.BaseResponse;
//import com.hbbhbank.moamoa.withdraw.dto.request.WithdrawRequestDto;
//import com.hbbhbank.moamoa.withdraw.dto.response.WithdrawResponseDto;
//import com.hbbhbank.moamoa.withdraw.service.WithdrawService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1/withdrawals")
//@RequiredArgsConstructor
//public class WithdrawController {
//
//  private final WithdrawService withdrawService;
//
//  /**
//   * 사용자의 포인트를 환비 외부 계좌로 출금합니다.
//   *
//   * @param req 출금 요청 정보 (지갑 통화, 금액 등)
//   * @return 출금 결과 정보 (통화 코드, 출금 금액, 거래 후 포인트, 출금 요청 시각)
//   */
//  @PostMapping
//  public ResponseEntity<BaseResponse<WithdrawResponseDto>> withdrawToLinkedAccount(
//    @Valid @RequestBody WithdrawRequestDto req) {
//
//    // 출금 서비스 실행 및 응답 생성
//    WithdrawResponseDto response = withdrawService.withdrawToRealAccount(req);
//
//    return ResponseEntity.ok(BaseResponse.success(response));
//  }
//
//  /**
//   * 출금할 지갑을 알고있는 상태에서의 환불 요청
//   */
//
//  /**
//   * 출금할 지갑을 모르고 있는 상태에서의 환불 요청
//   */
//}
