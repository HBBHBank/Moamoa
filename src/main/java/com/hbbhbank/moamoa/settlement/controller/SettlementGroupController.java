package com.hbbhbank.moamoa.settlement.controller;

import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.settlement.dto.request.CreateSettlementGroupRequestDto;
import com.hbbhbank.moamoa.settlement.dto.request.VerifyJoinCodeRequestDto;
import com.hbbhbank.moamoa.settlement.dto.response.CreateSettlementGroupResponseDto;
import com.hbbhbank.moamoa.settlement.dto.response.ReissueJoinCodeResponseDto;
import com.hbbhbank.moamoa.settlement.dto.response.SettlementTransactionResponseDto;
import com.hbbhbank.moamoa.settlement.dto.response.VerifyJoinCodeResponseDto;
import com.hbbhbank.moamoa.settlement.service.SettlementGroupService;
import com.hbbhbank.moamoa.transfer.dto.request.PointTransferRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.transaction.TransactionResponseDto;
import com.hbbhbank.moamoa.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/settlement-groups")
@RequiredArgsConstructor
public class SettlementGroupController {

  private final SettlementGroupService settlementGroupService;
  private final UserService userService;

  /**
   * 정산 그룹 생성
   */
  @PostMapping
  public ResponseEntity<BaseResponse<CreateSettlementGroupResponseDto>> createGroup(
    @RequestBody @Valid CreateSettlementGroupRequestDto request
  ) {
    return ResponseEntity.ok(
      BaseResponse.success(settlementGroupService.createGroup(request))
    );
  }

  /**
   * 초대 코드 유효성 검증
   */
  @PostMapping("/verify-code")
  public ResponseEntity<BaseResponse<VerifyJoinCodeResponseDto>> verifyJoinCode(
    @RequestBody @Valid VerifyJoinCodeRequestDto request
  ) {
    return ResponseEntity.ok(
      BaseResponse.success(settlementGroupService.verifyJoinCode(request))
    );
  }

  /**
   * 초대 코드 재발급
   */
  @PostMapping("/{groupId}/reissue-code")
  public ResponseEntity<BaseResponse<ReissueJoinCodeResponseDto>> reissueJoinCode(@PathVariable Long groupId) {
    return ResponseEntity.ok(
      BaseResponse.success(settlementGroupService.reissueJoinCode(groupId))
    );
  }

  /**
   * 정산 시작
   */
  @PostMapping("/{groupId}/start")
  public ResponseEntity<BaseResponse<Void>> startSettlement(@PathVariable Long groupId) {
    settlementGroupService.startSettlement(groupId);
    return ResponseEntity.ok(BaseResponse.success(null));
  }

  /**
   * 정산 취소
   */
  @PostMapping("/{groupId}/cancel")
  public ResponseEntity<BaseResponse<Void>> cancelSettlement(@PathVariable Long groupId) {
    settlementGroupService.cancelSettlement(groupId);
    return ResponseEntity.ok(BaseResponse.success(null));
  }

  /**
   * 정산 내역 조회
   */
  @GetMapping("/{groupId}/transactions")
  public ResponseEntity<BaseResponse<List<SettlementTransactionResponseDto>>> getSettlementTransactions(@PathVariable Long groupId) {
    return ResponseEntity.ok(BaseResponse.success(settlementGroupService.getSettlementTransactions(groupId)));
  }

  /**
   * 방장에게 송금
   */
  @PostMapping("/{groupId}/transfer")
  public ResponseEntity<BaseResponse<Void>> transferToHost(
    @PathVariable Long groupId,
    @RequestBody @Valid PointTransferRequestDto request
  ) {
    settlementGroupService.transferToHost(groupId, request);
    return ResponseEntity.ok(BaseResponse.success(null));
  }

  /**
   * 그룹 삭제 (정산 완료 상태 + 모두 송금 완료일 때만 가능)
   */
  @DeleteMapping("/{groupId}")
  public ResponseEntity<BaseResponse<Void>> deleteGroup(@PathVariable Long groupId) {
    settlementGroupService.deleteGroup(groupId);
    return ResponseEntity.ok(BaseResponse.success(null));
  }

  /**
   * 방장의 거래 내역 공유 조회 (정산 멤버만 접근 가능)
   */
  @GetMapping("/{groupId}/shared-transactions")
  public ResponseEntity<BaseResponse<List<TransactionResponseDto>>> getSharedTransactions(
    @PathVariable Long groupId
  ) {
    Long userId = userService.getCurrentUserId();
    List<TransactionResponseDto> txs = settlementGroupService.getSharedTransactions(groupId, userId);
    return ResponseEntity.ok(BaseResponse.success(txs));
  }

  /**
   * 정산 그룹 비활성화 (공유 중지)
   */
  @PostMapping("/{groupId}/deactivate")
  public ResponseEntity<BaseResponse<Void>> deactivateGroup(@PathVariable Long groupId) {
    settlementGroupService.deactivateGroup(groupId);
    return ResponseEntity.ok(BaseResponse.success(null));
  }

  /**
   * 정산 그룹 활성화 (공유 시작)
   */
  @PostMapping("/{groupId}/activate")
  public ResponseEntity<BaseResponse<Void>> activateGroup(@PathVariable Long groupId) {
    settlementGroupService.activateGroup(groupId);
    return ResponseEntity.ok(BaseResponse.success(null));
  }
}