package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.external.domain.UserAccountLink;
import com.hbbhbank.moamoa.external.dto.request.CreateVerificationContext;
import com.hbbhbank.moamoa.external.dto.response.GetVerificationCodeResponseDto;
import com.hbbhbank.moamoa.external.service.HwanbeeAccountService;
import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.user.service.UserService;
import com.hbbhbank.moamoa.wallet.domain.Currency;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.dto.request.CreateWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.GetVerificationCodeWithinMoamoaRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.SearchWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.GetWalletInfoResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.SearchWalletResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.CreateWalletResponseDto;
import com.hbbhbank.moamoa.wallet.exception.WalletErrorCode;
import com.hbbhbank.moamoa.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

  private final WalletRepository walletRepository;
  private final UserService userService;
  private final CurrencyService currencyService;
  private final HwanbeeAccountService hwanbeeAccountService;

  @Override
  public SearchWalletResponseDto getWalletByUserAndCurrency(SearchWalletRequestDto req) {
    Long userId = userService.getCurrentUserId();

    Wallet wallet = walletRepository.findByUserIdAndCurrencyCode(userId, req.currencyCode())
      .orElseThrow(() -> BaseException.type(WalletErrorCode.NOT_FOUND_WALLET));

    return SearchWalletResponseDto.from(wallet);
  }

  @Override
  @Transactional
  public List<SearchWalletResponseDto> getAllWalletsByUser() {
    Long userId = userService.getCurrentUserId();

    return walletRepository.findAllByUserWithCurrency(userId).stream()
      .map(SearchWalletResponseDto::from)
      .collect(Collectors.toList());
  }

  @Override
  public GetVerificationCodeResponseDto getVerificationCode(GetVerificationCodeWithinMoamoaRequestDto req) {
    Long userId = userService.getCurrentUserId(); // 사용자 id 받기

    return hwanbeeAccountService.getVerificationCodeFromHwanbee(userId, req);
  }

  @Override
  @Transactional
  public CreateWalletResponseDto createWalletAfterVerification(CreateWalletRequestDto req) {
    Long userId = userService.getCurrentUserId();
    User user = userService.getByIdOrThrow(userId);
    Currency currency = currencyService.getByCodeOrThrow(req.currencyCode());

    if (walletRepository.existsByUserIdAndCurrencyCode(userId, currency.getCode())) {
      throw BaseException.type(WalletErrorCode.DUPLICATE_WALLET);
    }

    UserAccountLink accountLink = hwanbeeAccountService.verifyAndLinkAccountWithUser(userId, CreateVerificationContext.from(req));
    String walletNumber = generateWalletNumber(userId, currency.getCode());
    Wallet wallet = Wallet.create(user, walletNumber, currency, accountLink);

    return CreateWalletResponseDto.from(walletRepository.save(wallet));
  }

  @Override
  public GetWalletInfoResponseDto getReceiverWalletInfo(String walletNumber) {
    Wallet wallet = walletRepository.findByWalletNumber(walletNumber)
      .orElseThrow(() -> BaseException.type(WalletErrorCode.NOT_FOUND_WALLET));

    return GetWalletInfoResponseDto.from(wallet);
  }

  @Override
  public Wallet getWalletByNumberOrThrow(String walletNumber) {
    return walletRepository.findByWalletNumber(walletNumber)
      .orElseThrow(() -> BaseException.type(WalletErrorCode.NOT_FOUND_WALLET));
  }


  private String generateWalletNumber(Long userId, String currencyCode) {
    int random = (int)(Math.random() * 90_000_000) + 10_000_000;
    return String.format("%s-%d-%08d", currencyCode, userId, random);
  }
}
