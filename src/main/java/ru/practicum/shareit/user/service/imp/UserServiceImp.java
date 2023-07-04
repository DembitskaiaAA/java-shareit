package ru.practicum.shareit.user.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImp(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User createUser(UserDto userDto) {
        User savedUser = userMapper.transformUserDtoToUser(userDto);
        return userRepository.save(savedUser);
    }

    @Override
    public String deleteUser(Long userId) {
        validUser(userId);
        userRepository.deleteById(userId);
        return String.format("Пользователь с id: %s удален", userId);
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        User user = validUser(userId);
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        return userRepository.save(user);
    }

    @Override
    public User getUser(Long userId) {
        return validUser(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User validUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь c id: %s отсутствует", userId)));
    }
}




