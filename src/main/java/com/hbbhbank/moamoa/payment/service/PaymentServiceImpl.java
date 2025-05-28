package com.hbbhbank.moamoa.payment.service;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.payment.domain.QrImage;
import com.hbbhbank.moamoa.payment.dto.request.PaymentRequestDto;
import com.hbbhbank.moamoa.payment.dto.response.QrCodeCreateResponseDto;
import com.hbbhbank.moamoa.payment.dto.response.QrCodeInfoResponseDto;
import com.hbbhbank.moamoa.payment.exception.PaymentErrorCode;
import com.hbbhbank.moamoa.payment.repository.QrImageRepository;
import com.hbbhbank.moamoa.payment.util.QRCodeUtil;
import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.wallet.domain.InternalWalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionStatus;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;
import com.hbbhbank.moamoa.wallet.exception.WalletErrorCode;
import com.hbbhbank.moamoa.wallet.repository.InternalWalletTransactionRepository;
import com.hbbhbank.moamoa.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

  private final QrImageRepository qrImageRepository;
  private final WalletRepository walletRepository;
  private final InternalWalletTransactionRepository internalWalletTransactionRepository;

  @Override
  @Transactional
  public QrCodeCreateResponseDto generateQr(Long walletId) {
    // 1. 링크 생성
    String qrLink = "http://localhost:8080/api/v1/payments/use?walletId=" + walletId;

    // 2. QR 이미지 생성
    byte[] imageBytes = QRCodeUtil.generateQRCodeImage(qrLink);

    // 3. DB에 저장
    QrImage savedQrImage = qrImageRepository.save(
      QrImage.builder()
        .qrImage(imageBytes)
        .build()
    );

    // 4. URL 생성
    String imageUrl = QRCodeUtil.getQrImageURL(savedQrImage);

    // 5. 응답 반환
    return new QrCodeCreateResponseDto(savedQrImage.getId(), imageUrl);
  }


  @Override
  @Transactional
  public void payWithQr(Long buyerUserId, String uuid, PaymentRequestDto req) {

    QrImage qrImage = qrImageRepository.findByUuid(uuid)
      .orElseThrow(() -> new BaseException(PaymentErrorCode.FAILED_CREATE_QR));

    if (qrImage.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new BaseException(PaymentErrorCode.QR_EXPIRED);
    }

    req.validate();

    Wallet sellerWallet = qrImage.getWallet();

    Wallet buyerWallet = walletRepository.findByUserIdAndCurrencyCode(buyerUserId, req.currencyCode())
      .orElseThrow(() -> new BaseException(WalletErrorCode.NOT_FOUND_WALLET));

    BigDecimal amount = req.amount();

    if (buyerWallet.getBalance().compareTo(amount) < 0) {
      throw new BaseException(WalletErrorCode.INSUFFICIENT_BALANCE);
    }

    buyerWallet.decreaseBalance(amount);
    sellerWallet.increaseBalance(amount);

    walletRepository.saveAll(List.of(buyerWallet, sellerWallet));

    // 거래 기록
    internalWalletTransactionRepository.save(
      InternalWalletTransaction.create(
        buyerWallet, sellerWallet, WalletTransactionType.QR_PAYMENT, WalletTransactionStatus.SUCCESS, amount.negate()
      )
    );
    internalWalletTransactionRepository.save(
      InternalWalletTransaction.create(
        sellerWallet, buyerWallet, WalletTransactionType.QR_PAYMENT, WalletTransactionStatus.SUCCESS, amount
      )
    );
  }

  // QR 코드 이미지 반환
  @Override
  @Transactional(readOnly = true)
  public byte[] getQRCodeImage(Long qrId) {
    return qrImageRepository.findById(qrId)
      .orElseThrow(() -> new BaseException(PaymentErrorCode.FAILED_CREATE_QR))
      .getQrImage();
  }

  @Override
  @Transactional
  public QrCodeCreateResponseDto reissueQr(Long walletId) {
    return generateQr(walletId); // 단순히 새로 발급
  }

  // QR 스캔 후 판매자 정보 조회 (이름)
  @Override
  @Transactional(readOnly = true)
  public QrCodeInfoResponseDto getQrInfo(String uuid) {
    QrImage qrImage = qrImageRepository.findByUuid(uuid)
      .orElseThrow(() -> new BaseException(PaymentErrorCode.FAILED_CREATE_QR));

    if (qrImage.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new BaseException(PaymentErrorCode.QR_EXPIRED);
    }

    Wallet wallet = qrImage.getWallet();
    User owner = wallet.getUser();

    return new QrCodeInfoResponseDto(
      wallet.getId(),
      owner.getName(),
      wallet.getCurrency().getCode()
    );
  }
}
