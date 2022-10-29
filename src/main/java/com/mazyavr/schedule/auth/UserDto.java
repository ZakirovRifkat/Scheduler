package com.mazyavr.schedule.auth;


public class UserDto {
    public long id;
    public String username;

    public static UserDto fromUser(User user) {
        var userDto = new UserDto();
        userDto.id = user.getId();
        userDto.username = user.getUsername();
        return userDto;
    }
}
