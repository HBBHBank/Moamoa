package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.client.common.HwanbeeApiClient;
import com.hbbhbank.moamoa.external.client.common.HwanbeeApiEndpoints;
import com.hbbhbank.moamoa.external.dto.request.transfer.TransferRequestDto;
import com.hbbhbank.moamoa.external.dto.response.transfer.TransferResponseDto;
import com.hbbhbank.moamoa.external.exception.HwanbeeErrorCode;
import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HwanbeeTransferClientImpl implements HwanbeeTransferClient {

  private final HwanbeeApiClient apiClient;
  private final HwanbeeApiEndpoints endpoints;

  @Override
  public TransferResponseDto requestTransfer(TransferRequestDto dto) {
    BaseResponse<TransferResponseDto> response = apiClient.post(
      endpoints.getTransferUrl(),
      dto,
      new ParameterizedTypeReference<>() {},
      HwanbeeErrorCode.TRANSFER_FAILED
    );

    validateResponse(response);
    return response.getResult();
  }

  private void validateResponse(BaseResponse<TransferResponseDto> response) {
    if (!response.getResult().isSuccess()) {
      throw BaseException.type(HwanbeeErrorCode.TRANSFER_FAILED);
    }
  }
}
