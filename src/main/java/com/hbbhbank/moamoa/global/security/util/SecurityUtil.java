package com.hbbhbank.moamoa.global.security.util;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.global.exception.GlobalErrorCode;
import com.hbbhbank.moamoa.global.security.info.JwtUserInfo;
import com.hbbhbank.moamoa.global.security.principal.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring Security의 SecurityContextHolder에 접근하여
 * 인증 정보를 설정하거나 조회하는 유틸 클래스입니다.
 */
public class SecurityUtil {

  /**
   * 현재 SecurityContext에 인증 객체를 설정합니다.
   * 일반적으로 JWT 필터에서 인증이 완료된 사용자 정보를 설정할 때 사용됩니다.
   *
   * @param authentication 인증 완료된 Authentication 객체
   */
  public static void setAuthentication(Authentication authentication) {
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  /**
   * 현재 인증된 사용자 정보를 반환합니다.
   * 인증되지 않은 경우 null 반환.
   *
   * @return 현재 사용자 정보 또는 null
   */
  public static Object getCurrentPrincipal() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }
    return authentication.getPrincipal();
  }

  /**
   * 현재 인증된 사용자의 ID를 가져오는 메서드
   *
   * @return 사용자 식별자 또는 null
   */
  public static Long getCurrentUserId() {
    Object principal = getCurrentPrincipal();
    if (principal instanceof UserPrincipal userPrincipal) {
      return userPrincipal.getUserId(); // → UserPrincipal에서 userId 추출
    }
    throw new BaseException(GlobalErrorCode.UNAUTHORIZED);
  }

  /**
   * 현재 인증 객체 자체를 가져옵니다.
   *
   * @return Authentication 또는 null
   */
  public static Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }
}

