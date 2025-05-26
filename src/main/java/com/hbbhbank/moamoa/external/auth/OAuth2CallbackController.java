package com.hbbhbank.moamoa.external.auth;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.global.security.util.SecurityUtil;
import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.user.exception.UserErrorCode;
import com.hbbhbank.moamoa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class OAuth2CallbackController {

  private final UserRepository userRepository;
  private final OAuth2TokenService oAuth2TokenService;

  /**
   * 환비 인가 서버가 인가 코드 발급 후 이 URI로 리디렉션함
   * → code로 access/refresh token 발급
   */
  @GetMapping("/callback")
  public ResponseEntity<String> oauthCallback(
    @RequestParam String code,
    @RequestParam String state // 추후 state 검증용
  ) {
    Long userId = SecurityUtil.getCurrentUserId(); // 로그인된 사용자
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));

    String token = oAuth2TokenService.ensureAccessToken(user, code);
    return ResponseEntity.ok("토큰 저장 완료: " + token);
  }
}

