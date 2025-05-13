package com.hbbhbank.moamoa.withdraw.controller;

import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.withdraw.dto.request.WithdrawRequestDto;
import com.hbbhbank.moamoa.withdraw.dto.response.WithdrawResponseDto;
import com.hbbhbank.moamoa.withdraw.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/withdraws")
@RequiredArgsConstructor
public class WithdrawController {

  private final WithdrawService withdrawService;

  @PostMapping
  public ResponseEntity<BaseResponse> withdraw(@RequestBody WithdrawRequestDto req) {
    WithdrawResponseDto response = withdrawService.withdrawToRealAccount(req);
    return ResponseEntity.ok(BaseResponse.success(response));
  }
}
