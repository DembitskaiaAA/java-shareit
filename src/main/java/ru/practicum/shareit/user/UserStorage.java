package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    UserDto createUser(User user);

    String deleteUser(Long userId);

    UserDto updateUser(Long userId, User user);

    UserDto getUser(Long userId);

    List<UserDto> getAllUsers();

    Map<Long, User> getUsers();
}
