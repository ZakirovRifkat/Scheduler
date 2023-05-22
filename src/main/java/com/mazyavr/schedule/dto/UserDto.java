package com.mazyavr.schedule.dto;

import com.mazyavr.schedule.entity.UserEntity;

public record UserDto(
    long id,
    String name,
    String email,
    String refresh_token) {

  public static UserDto fromEntity(UserEntity entity) {
    return new UserDto(entity.getId(), entity.getName(), entity.getEmail(),
        entity.getRefreshToken());
  }
}
