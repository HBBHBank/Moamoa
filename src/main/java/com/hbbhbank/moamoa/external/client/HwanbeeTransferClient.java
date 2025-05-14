package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.dto.request.transfer.TransferRequestDto;
import com.hbbhbank.moamoa.external.dto.response.transfer.TransferResponseDto;

public interface HwanbeeTransferClient {

  /**
   * 환비 API에 송금 요청을 전송합니다.
   *
   * @param dto 송금 요청 정보 (보내는 계좌, 받는 계좌, 금액, 통화, 요청 시간 등)
   * @return 송금 결과를 담은 응답 객체 (거래 상태, 거래 ID, 거래 완료 시각, 거래 후 잔액 등)
   * @throws com.hbbhbank.moamoa.global.exception.BaseException 송금 실패 또는 외부 API 오류 발생 시
   */
  TransferResponseDto requestTransfer(TransferRequestDto dto);
}
