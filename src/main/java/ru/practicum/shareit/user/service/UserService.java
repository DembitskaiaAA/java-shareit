package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(UserDto userDto);

    String deleteUser(Long userId);

    User updateUser(Long userId, UserDto userDto);

    User getUser(Long userId);

    List<User> getAllUsers();

    User validUser(Long userId);
}
