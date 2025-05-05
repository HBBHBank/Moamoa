package com.hbbhbank.moamoa.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 포스트맨으로 테스트하기 위해 비밀번호를 암호화하는 클래스
// 개발 완료 시 삭제 예정
public class PasswordHasher {
  public static void main(String[] args) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String hash = encoder.encode("securePass123");
    System.out.println("암호화된 비밀번호: " + hash);
  }
}