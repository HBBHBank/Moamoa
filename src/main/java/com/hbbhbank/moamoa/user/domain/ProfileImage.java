package com.hbbhbank.moamoa.user.domain;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.user.exception.UserErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ProfileImage {
  IMAGE1("GOM_1", "img_gom_1"),
  IMAGE2("GOM_2", "img_gom_2"),
  IMAGE3("GOM_3", "img_gom_3"),
  IMAGE4("GOM_4", "img_gom_4");

  private final String key;
  private final String value;

  // 프론트에서 전달받은 프로필 이미지 값으로 enum을 찾는 메서드
  public static ProfileImage from(String value) {
    for (ProfileImage profileImage : ProfileImage.values()) {
      if (profileImage.getValue().equals(value)) {
        return profileImage;
      }
    }
    throw BaseException.type(UserErrorCode.NOT_FOUND_PROFILE_IMAGE);
  }
}
