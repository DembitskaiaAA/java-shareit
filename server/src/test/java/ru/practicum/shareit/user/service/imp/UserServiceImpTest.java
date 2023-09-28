package ru.practicum.shareit.user.service.imp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {
    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @InjectMocks
    private UserServiceImp userService;


    @Test
    public void testCreateUser_success() {

        UserDto userDto = new UserDto(1L, "John", "john@example.com");

        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        when(userMapper.transformUserDtoToUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userMapper).transformUserDtoToUser(userDto);
        verify(userRepository).save(user);
    }

    @Test
    public void testDeleteUser_userNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testUpdateUser_success() {
        Long userId = 1L;

        UserDto userDto = new UserDto(1L, "John", "john@example.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Jane");
        existingUser.setEmail("jane@example.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John");
        updatedUser.setEmail("john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userService.updateUser(userId, userDto);

        assertNotNull(result);
        assertEquals(updatedUser.getId(), result.getId());
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getEmail(), result.getEmail());

        verify(userRepository).findById(userId);
        verify(userRepository).save(updatedUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testUpdateUser_userNotFound() {
        Long userId = 1L;

        UserDto userDto = new UserDto(1L, "John", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, userDto));

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testGetUserSuccess() {
        Long userId = 1L;

        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setName("John");
        expectedUser.setEmail("john@example.com");

        given(userRepository.findById(userId)).willReturn(Optional.of(expectedUser));

        User actualUser = userService.getUser(userId);

        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    void testGetUserNotFound() {
        Long userId = 1L;

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Пользователь c id: %s отсутствует", userId));
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane");
        user2.setEmail("jane@example.com");

        List<User> expectedUsers = Arrays.asList(user1, user2);

        given(userRepository.findAll()).willReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers();

        assertThat(actualUsers).hasSize(expectedUsers.size());

        assertThat(actualUsers.get(0).getId()).isEqualTo(expectedUsers.get(0).getId());
        assertThat(actualUsers.get(0).getName()).isEqualTo(expectedUsers.get(0).getName());
        assertThat(actualUsers.get(0).getEmail()).isEqualTo(expectedUsers.get(0).getEmail());

        assertThat(actualUsers.get(1).getId()).isEqualTo(expectedUsers.get(1).getId());
        assertThat(actualUsers.get(1).getName()).isEqualTo(expectedUsers.get(1).getName());
        assertThat(actualUsers.get(1).getEmail()).isEqualTo(expectedUsers.get(1).getEmail());
    }
}