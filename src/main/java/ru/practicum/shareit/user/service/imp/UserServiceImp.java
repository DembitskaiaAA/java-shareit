package ru.practicum.shareit.user.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.UpdateUser;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(User user) {
        User savedUser = userRepository.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public String deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return String.format("Пользователь с id: %s удален", userId);
        } else {
            throw new NotFoundException(
                    String.format("При удалении пользователя ошибка: пользователь c id: %s отсутствует", userId));
        }
    }

    @Override
    public UserDto updateUser(Long userId, UpdateUser updateUser) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("При обновлении пользователя ошибка: пользователь c id: %s отсутствует", userId)));

        if (updateUser.getEmail() != null) {
            user.setEmail(updateUser.getEmail());
        }

        if (updateUser.getName() != null) {
            user.setName(updateUser.getName());
        }

        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUser(Long userId) {
        if (userRepository.existsById(userId)) {
            User savedUser = userRepository.findById(userId).get();
            return UserMapper.toUserDto(savedUser);
        } else {
            throw new NotFoundException(
                    String.format("При удалении получении пользователя ошибка: пользователь c id: %s отсутствует", userId));
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> savedUsers = userRepository.findAll();
        return savedUsers.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
