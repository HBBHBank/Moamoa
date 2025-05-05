package com.hbbhbank.moamoa.global.security.principal;

import com.hbbhbank.moamoa.user.domain.ERole;
import com.hbbhbank.moamoa.user.projection.UserSecurityForm;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Builder
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

  private final Long userId;
  private final ERole role;

  /**
   * Spring Security의 권한 목록
   * ROLE_ 접두어 필수 (예: ROLE_USER, ROLE_ADMIN)
   */
  private final Collection<? extends GrantedAuthority> authorities;

  /**
   * UserSecurityForm을 기반으로 UserPrincipal 생성
   */
  public static UserPrincipal create(UserSecurityForm securityForm) {
    String authority = "ROLE_" + securityForm.getRole().name(); // ROLE_USER
    return UserPrincipal.builder()
      .userId(securityForm.getId())
      .role(securityForm.getRole())
      .authorities(Collections.singleton(new SimpleGrantedAuthority(authority)))
      .build();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  /**
   * 비밀번호는 JWT 기반 인증에서는 필요하지 않으므로 null 반환
   */
  @Override
  public String getPassword() {
    return null;
  }

  /**
   * userId를 username처럼 사용
   */
  @Override
  public String getUsername() {
    return String.valueOf(userId);
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
