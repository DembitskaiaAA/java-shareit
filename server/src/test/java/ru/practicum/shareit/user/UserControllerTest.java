package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void createUserTest() throws Exception {
        UserDto userDto = new UserDto(1L, "John Doe", "johndoe@example.com");
        User user = new User(1L, "John Doe", "johndoe@example.com");

        given(userService.createUser(any(UserDto.class))).willReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("johndoe@example.com")));
    }

    @Test
    public void testDeleteUser() throws Exception {
        Long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService, Mockito.times(1)).deleteUser(userId);
    }

    @Test
    public void testUpdateUser() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "John Smith", "john.smith@gmail.com");
        User expectedUser = new User(userId, userDto.getName(), userDto.getEmail());

        Mockito.when(userService.updateUser(userId, userDto)).thenReturn(expectedUser);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Smith")))
                .andExpect(jsonPath("$.email", is("john.smith@gmail.com")));

    }

    @Test
    public void testGetUser() throws Exception {
        Long userId = 1L;
        User mockUser = new User(userId, "John Doe", "john.doe@example.com");
        given(userService.getUser(userId)).willReturn(mockUser);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.name", is(mockUser.getName())))
                .andExpect(jsonPath("$.email", is(mockUser.getEmail())));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        User user1 = new User(1L, "John Doe", "john.doe@example.com");
        User user2 = new User(2L, "Jane Doe", "jane.doe@example.com");
        List<User> userList = Arrays.asList(user1, user2);
        given(userService.getAllUsers()).willReturn(userList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(user1.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(user1.getName())))
                .andExpect(jsonPath("$[0].email", is(user1.getEmail())))
                .andExpect(jsonPath("$[1].id", is(user2.getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(user2.getName())))
                .andExpect(jsonPath("$[1].email", is(user2.getEmail())));
    }


}