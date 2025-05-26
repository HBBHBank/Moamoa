package com.hbbhbank.moamoa.transfer.controller;

import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.transfer.dto.request.PointTransferRequestDto;
import com.hbbhbank.moamoa.transfer.dto.response.PointTransferResponseDto;
import com.hbbhbank.moamoa.transfer.service.PointTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfers")
public class PointTransferController {

  private final PointTransferService pointTransferService;

  /**
   * 사용자 포인트 송금 요청
   */
  @PostMapping("/points")
  public ResponseEntity<BaseResponse<PointTransferResponseDto>> transferPoints(
    @RequestBody @Valid PointTransferRequestDto requestDto
  ) {
    PointTransferResponseDto responseDto = pointTransferService.transferByUser(requestDto);
    return ResponseEntity.ok(BaseResponse.success(responseDto));
  }
}