package ru.practicum.shareit.user.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class UserServiceImp implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImp(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto createUser(User user) {
        return userStorage.createUser(user);
    }

    @Override
    public String deleteUser(Long userId) {
        return userStorage.deleteUser(userId);
    }

    @Override
    public UserDto updateUser(Long userId, User user) {
        return userStorage.updateUser(userId, user);
    }

    @Override
    public UserDto getUser(Long userId) {
        return userStorage.getUser(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers();
    }
}
