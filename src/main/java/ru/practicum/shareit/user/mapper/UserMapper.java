package ru.practicum.shareit.user.mapper;


import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDtoForUpdate;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;


@UtilityClass
public class UserMapper {
    public static UserResponseDto toUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static User toUserDao(UserRequestDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static User toUserDaoUpdate(UserRequestDtoForUpdate userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static UserRequestDto toUserRequestDto(User user) {
        return UserRequestDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
