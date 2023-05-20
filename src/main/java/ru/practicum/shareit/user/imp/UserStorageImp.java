package ru.practicum.shareit.user.imp;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.RepeatedValueException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserStorageImp implements UserStorage {
    Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public UserDto createUser(User user) {
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            if (entry.getValue().getEmail().equals(user.getEmail())) {
                throw new RepeatedValueException(
                        String.format("При добавлении пользователя ошибка: пользователь c email: %s уже существует", user.getEmail()));
            }
        }
        user.setId(++id);
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public String deleteUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(
                    String.format("При удалении пользователя ошибка: пользователь c id: %s отсутствует", userId));
        }
        users.remove(userId);
        return String.format("Пользователь с id: %s удален", userId);
    }

    @Override
    public UserDto updateUser(Long userId, User user) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(
                    String.format("При обновлении пользователя ошибка: пользователь c id: %s отсутствует", userId));
        }
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            if (entry.getValue().getEmail().equals(user.getEmail()) && entry.getValue().getId() != userId) {
                throw new RepeatedValueException(
                        String.format("При добавлении пользователя ошибка: пользователь c email: %s уже существует",
                                user.getEmail()));
            }
        }
        User resultUser = users.get(userId);
        Optional.ofNullable(user.getName()).ifPresent(resultUser::setName);
        Optional.ofNullable(user.getEmail()).ifPresent(resultUser::setEmail);
        users.put(userId, resultUser);
        return UserMapper.toUserDto(resultUser);
    }

    @Override
    public UserDto getUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(
                    String.format("При получении пользователя ошибка: пользователь c id: %s отсутствует", userId));
        }
        return UserMapper.toUserDto(users.get(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
