package com.hbbhbank.moamoa.external.service;

import com.hbbhbank.moamoa.external.client.HwanbeeTransferClientImpl;
import com.hbbhbank.moamoa.external.dto.request.transfer.TransferRequestDto;
import com.hbbhbank.moamoa.external.dto.response.transfer.TransferResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HwanbeeTransferService {

  private final HwanbeeTransferClientImpl hwanbeeTransferClient;

  public TransferResponseDto transfer(TransferRequestDto dto) {
    return hwanbeeTransferClient.requestTransfer(dto);
  }
}