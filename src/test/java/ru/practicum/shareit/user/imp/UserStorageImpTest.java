package ru.practicum.shareit.user.imp;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.RepeatedValueException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserStorageImpTest {

    private UserStorageImp userStorage;

    @Before
    @Autowired
    public void setUp() {
        userStorage = new UserStorageImp();
    }

    @Test
    public void testCreateUser() throws RepeatedValueException {

        User user1 = new User();
        user1.setName("Иван");
        user1.setEmail("ivan@example.com");

        UserDto resultDto1 = userStorage.createUser(user1);

        assertNotNull(resultDto1);
        assertEquals(1L, resultDto1.getId());
        assertEquals(user1.getName(), resultDto1.getName());
        assertEquals(user1.getEmail(), resultDto1.getEmail());

        User user2 = new User();
        user2.setName("Петр");
        user2.setEmail("ivan@example.com");

        try {
            userStorage.createUser(user2);
            fail("Expected RepeatedValueException was not thrown");
        } catch (RepeatedValueException e) {
            assertEquals(String.format("При добавлении пользователя ошибка: пользователь c email: %s уже существует", user2.getEmail()), e.getMessage());
        }

        User user3 = new User();
        user3.setName("Елена");
        user3.setEmail("elena@example.com");

        UserDto resultDto3 = userStorage.createUser(user3);

        assertNotNull(resultDto3);
        assertEquals(2L, resultDto3.getId());
        assertEquals(user3.getName(), resultDto3.getName());
        assertEquals(user3.getEmail(), resultDto3.getEmail());
    }

    @Test
    public void testDeleteUserSuccessfully() throws RepeatedValueException, NotFoundException {
        User user = new User();
        user.setName("Иван");
        user.setEmail("ivan@example.com");

        UserDto resultDto = userStorage.createUser(user);

        String resultMessage = userStorage.deleteUser(resultDto.getId());

        assertEquals(String.format("Пользователь с id: %s удален", resultDto.getId()), resultMessage);
        assertFalse(userStorage.getAllUsers().stream().anyMatch(x -> x.getId() == resultDto.getId()));
    }

    @Test
    public void testDeleteNonExistentUser() {
        try {
            userStorage.deleteUser(1L);
            fail("Expected NotFoundException was not thrown");
        } catch (NotFoundException e) {
            assertEquals("При удалении пользователя ошибка: пользователь c id: 1 отсутствует", e.getMessage());
        }
    }

    @Test
    public void testUpdateUserSuccessfully() throws RepeatedValueException, NotFoundException {
        User user = new User();
        user.setName("Иван");
        user.setEmail("ivan@example.com");

        UserDto resultDto = userStorage.createUser(user);

        user.setName("Петр");

        UserDto updatedDto = userStorage.updateUser(resultDto.getId(), user);

        assertNotNull(updatedDto);
        assertEquals(resultDto.getId(), updatedDto.getId());
        assertEquals(user.getName(), updatedDto.getName());
        assertEquals(user.getEmail(), updatedDto.getEmail());

        User updatedUser = userStorage.getUsers().get(resultDto.getId());

        assertNotNull(updatedUser);
        assertEquals(user.getName(), updatedUser.getName());
        assertEquals(user.getEmail(), updatedUser.getEmail());
    }

    @Test
    public void testUpdateUserWithRepeatedEmail() throws RepeatedValueException, NotFoundException {
        User user1 = new User();
        user1.setName("Иван");
        user1.setEmail("ivan@example.com");

        User user2 = new User();
        user2.setName("Петр");
        user2.setEmail("petr@example.com");

        userStorage.createUser(user1);
        userStorage.createUser(user2);

        user1.setEmail(user2.getEmail());

        try {
            userStorage.updateUser(user1.getId(), user1);
            fail("Expected RepeatedValueException was not thrown");
        } catch (RepeatedValueException e) {
            assertEquals(
                    String.format("При обновлении пользователя ошибка: пользователь c email: %s уже существует", user2.getEmail()),
                    e.getMessage());
        }
    }

    @Test
    public void testUpdateNonExistentUser() throws RepeatedValueException, NotFoundException {
        User user = new User();
        user.setName("Иван");
        user.setEmail("ivan@example.com");

        try {
            userStorage.updateUser(1L, user);
            fail("Expected NotFoundException was not thrown");
        } catch (NotFoundException e) {
            assertEquals("При обновлении пользователя ошибка: пользователь c id: 1 отсутствует", e.getMessage());
        }
    }

    @Test
    public void testGetUserForExistingUser() {
        User user = new User();
        user.setName("Иван");
        user.setEmail("ivan@example.com");

        userStorage.createUser(user);

        UserDto resultDto = userStorage.getUser(1L);

        assertEquals(user.getId(), resultDto.getId());
        assertEquals(user.getName(), resultDto.getName());
        assertEquals(user.getEmail(), resultDto.getEmail());
    }

    @Test
    public void testGetUserForNonexistentUser() {
        User user = new User();
        user.setName("Иван");
        user.setEmail("ivan@example.com");

        userStorage.createUser(user);

        try {
            userStorage.getUser(2L);
            fail("Expected NotFoundException was not thrown");
        } catch (NotFoundException e) {
            assertEquals(
                    String.format("При получении пользователя ошибка: пользователь c id: %d отсутствует", 2L),
                    e.getMessage());
        }
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Иван");
        user1.setEmail("ivan@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Петр");
        user2.setEmail("petr@example.com");

        userStorage.createUser(user1);
        userStorage.createUser(user2);

        List<UserDto> resultDtos = userStorage.getAllUsers();

        assertEquals(2, resultDtos.size());
        assertEquals(user1.getId(), resultDtos.get(0).getId());
        assertEquals(user1.getName(), resultDtos.get(0).getName());
        assertEquals(user1.getEmail(), resultDtos.get(0).getEmail());
        assertEquals(user2.getId(), resultDtos.get(1).getId());
        assertEquals(user2.getName(), resultDtos.get(1).getName());
        assertEquals(user2.getEmail(), resultDtos.get(1).getEmail());
    }

    @Test
    public void testGetUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Иван");
        user1.setEmail("ivan@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Петр");
        user2.setEmail("petr@example.com");

        userStorage.createUser(user1);
        userStorage.createUser(user2);

        Map<Long, User> resultUsers = userStorage.getUsers();

        assertEquals(2, resultUsers.size());
        assertEquals(user1.getId(), resultUsers.get(user1.getId()).getId());
        assertEquals(user1.getName(), resultUsers.get(user1.getId()).getName());
        assertEquals(user1.getEmail(), resultUsers.get(user1.getId()).getEmail());
        assertEquals(user2.getId(), resultUsers.get(user2.getId()).getId());
        assertEquals(user2.getName(), resultUsers.get(user2.getId()).getName());
        assertEquals(user2.getEmail(), resultUsers.get(user2.getId()).getEmail());
    }
}