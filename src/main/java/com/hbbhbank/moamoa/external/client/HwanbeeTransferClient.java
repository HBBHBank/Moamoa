package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.dto.request.TransferRequestDto;
import com.hbbhbank.moamoa.external.dto.response.TransferResponseDto;
import com.hbbhbank.moamoa.external.exception.HwanbeeErrorCode;
import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HwanbeeTransferClient {

  private final HwanbeeApiClient apiClient;

  @Value("https://api.hwanbee.com/v1/transfer")
  private String transferUrl;

  public TransferResponseDto requestTransfer(TransferRequestDto dto) {
    BaseResponse<TransferResponseDto> response = apiClient.postForBaseResponse(
      transferUrl,
      dto,
      new ParameterizedTypeReference<>() {},
      HwanbeeErrorCode.WITHDRAWAL_FAILED
    );

    if (!"SUCCESS".equals(response.getResult().status())) {
      throw BaseException.type(HwanbeeErrorCode.WITHDRAWAL_FAILED);
    }

    return response.getResult();
  }
}