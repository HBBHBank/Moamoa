package com.hbbhbank.moamoa.payment.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.payment.domain.QrImage;
import com.hbbhbank.moamoa.payment.exception.PaymentErrorCode;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class QRCodeUtil {
  /**
   * 주어진 링크를 인코딩하여 QR 코드 이미지를 생성하고,
   * 그 이미지를 byte 배열 형태로 반환하는 메서드
   * @param link
   * @return QR 코드 이미지를 바이트 배열 형태로 변환
   * @throws PaymentErrorCode
   */
  public static byte[] generateQRCodeImage(String link) {
    try {
      int width = 200, height = 200;

      // QR코드 생성 옵션 설정
      Map<EncodeHintType, Object> hintMap = new HashMap<>();
      hintMap.put(EncodeHintType.MARGIN, 0);
      hintMap.put(EncodeHintType.CHARACTER_SET,"UTF-8");

      // QR 코드 생성
      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      BitMatrix bitMatrix = qrCodeWriter.encode(link, BarcodeFormat.QR_CODE, width, height, hintMap);

      // QR 코드 이미지 생성
      BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

      // QR 코드 이미지를 바이트 배열로 변환, byteArrayOutputStream 에 저장
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ImageIO.write(qrCodeImage,"png", byteArrayOutputStream);
      byteArrayOutputStream.flush();

      byte[] qrCodeBytes = byteArrayOutputStream.toByteArray();
      byteArrayOutputStream.close();

      return qrCodeBytes;

    } catch (Exception e) {
      log.info("QRCode Error", e);
      throw new BaseException(PaymentErrorCode.FAILED_CREATE_QR);
    }
  }

  /**
   * 큐알 코드 이미지 얻을 수 있는 api endpoint를 담은 url 반환
   * @param qrImage
   * @return
   */
  public static String getQrImageURL(QrImage qrImage) {
    return "http://localhost:8080/api/v1/payments/qr-code-images/" + qrImage.getId();
  }
}
