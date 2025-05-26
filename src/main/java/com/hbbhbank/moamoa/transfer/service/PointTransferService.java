package com.hbbhbank.moamoa.transfer.service;

import com.hbbhbank.moamoa.transfer.dto.request.PointTransferRequestDto;
import com.hbbhbank.moamoa.transfer.dto.response.PointTransferResponseDto;

public interface PointTransferService {

  PointTransferResponseDto transferByUser(PointTransferRequestDto dto);
}
