package com.hbbhbank.moamoa.recharge.controller;

import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.recharge.dto.request.RechargeRequestDto;
import com.hbbhbank.moamoa.recharge.dto.response.RechargeResponseDto;
import com.hbbhbank.moamoa.recharge.service.RechargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recharges")
@RequiredArgsConstructor
public class RechargeController {

  private final RechargeService rechargeService;

  /**
   * 직접 충전 요청 - 원화 지갑만 가능
   */
  @PostMapping("/direct")
  public ResponseEntity<BaseResponse> directRecharge(@RequestBody RechargeRequestDto request) {
    RechargeResponseDto response = rechargeService.directCharge(request);
    return ResponseEntity.ok(BaseResponse.success(response));
  }
}
