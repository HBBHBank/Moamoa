package com.hbbhbank.moamoa.transfer.service;

import com.hbbhbank.moamoa.transfer.dto.request.PointTransferRequestDto;
import com.hbbhbank.moamoa.transfer.dto.response.PointTransferResponseDto;
import com.hbbhbank.moamoa.wallet.domain.Wallet;

import java.math.BigDecimal;

public interface PointTransferService {

  // 외부 요청 (사용자 직접 송금)
  PointTransferResponseDto transferByUser(PointTransferRequestDto request);

  // 내부 로직에서 호출하는 송금 (지갑 객체 전달, 정산, 결제 등에서 이용)
  void transferInternally(Wallet from, Wallet to, BigDecimal amount);
}
