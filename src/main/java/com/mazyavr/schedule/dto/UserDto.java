package com.mazyavr.schedule.dto;

import com.mazyavr.schedule.entity.UserEntity;

public record UserDto(long id, String googleId, String refreshToken) {

  public static UserDto fromEntity(UserEntity entity) {
    return new UserDto(entity.getId(), entity.getGoogleId(), entity.getRefreshToken());
  }
}
