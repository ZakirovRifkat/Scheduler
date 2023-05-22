package com.mazyavr.schedule.dto;

import com.mazyavr.schedule.entity.UserEntity;

public record UserDto(long id, long email, String token) {

  public static UserDto fromEntity(UserEntity entity) {
    return new UserDto(entity.getId(), entity.getEmail(), entity.getToken());
  }
}
