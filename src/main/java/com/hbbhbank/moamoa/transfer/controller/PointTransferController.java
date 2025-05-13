package com.hbbhbank.moamoa.transfer.controller;

import com.hbbhbank.moamoa.transfer.dto.request.PointTransferRequestDto;
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

  @PostMapping("/points")
  public ResponseEntity<Void> transferPoints(@RequestBody @Valid PointTransferRequestDto requestDto) {
    pointTransferService.transferPoints(requestDto);
    return ResponseEntity.noContent().build();
  }
}
