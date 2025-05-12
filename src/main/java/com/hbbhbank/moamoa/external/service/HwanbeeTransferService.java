package com.hbbhbank.moamoa.external.service;

import com.hbbhbank.moamoa.external.client.HwanbeeTransferClient;
import com.hbbhbank.moamoa.external.dto.request.TransferRequestDto;
import com.hbbhbank.moamoa.external.dto.response.TransferResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HwanbeeTransferService {

  private final HwanbeeTransferClient hwanbeeTransferClient;

  /**
   * 환비 API에 송금 요청
   */
  public TransferResponseDto transfer(TransferRequestDto dto) {
    return hwanbeeTransferClient.requestTransfer(dto);
  }
}