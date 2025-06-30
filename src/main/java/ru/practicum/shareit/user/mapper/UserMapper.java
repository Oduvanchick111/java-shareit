package ru.practicum.shareit.user.mapper;


import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDtoForUpdate;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.UserDao;


@UtilityClass
public class UserMapper {
    public static UserResponseDto toUserResponseDto(UserDao userDao) {
        return UserResponseDto.builder()
                .id(userDao.getId())
                .email(userDao.getEmail())
                .name(userDao.getName())
                .build();
    }

    public static UserDao toUserDao(UserRequestDto userDto) {
        return UserDao.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static UserDao toUserDaoUpdate(UserRequestDtoForUpdate userDto) {
        return UserDao.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }
}
