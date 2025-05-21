//package com.hbbhbank.moamoa.transfer.controller;
//
//import com.hbbhbank.moamoa.global.common.BaseResponse;
//import com.hbbhbank.moamoa.transfer.dto.request.PointTransferRequestDto;
//import com.hbbhbank.moamoa.transfer.dto.response.PointTransferResponseDto;
//import com.hbbhbank.moamoa.transfer.service.PointTransferService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/transfers")
//public class PointTransferController {
//
//  private final PointTransferService pointTransferService;
//
//  /**
//   * 사용자 포인트 송금 요청
//   * @param requestDto 송금 요청 정보 (보내는/받는 지갑 번호, 금액)
//   * @return 송금 결과 정보
//   */
//  @PostMapping("/points")
//  public ResponseEntity<BaseResponse<PointTransferResponseDto>> transferPoints(
//    @RequestBody @Valid PointTransferRequestDto requestDto
//  ) {
//    PointTransferResponseDto responseDto = pointTransferService.transferByUser(requestDto);
//    return ResponseEntity.ok(BaseResponse.success(responseDto));
//  }
//
//  /**
//   * 송금할 지갑을 알고있는 상태에서의 환불 요청
//   */
//
//  /**
//   * 송금할 지갑을 모르고 있는 상태에서의 환불 요청
//   */
//}
