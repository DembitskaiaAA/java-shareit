package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestMapperTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);

    @Test
    public void testTransformItemRequestDtoToItemRequest() {
        Long userId = 1L;
        User user = new User(userId, "John Doe", "john@email.com");
        when(userService.getUser(userId)).thenReturn(user);

        LocalDateTime created = LocalDateTime.now();
        String description = "Test request";
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, description, userId, created, null);

        ItemRequest itemRequest = itemRequestMapper.transformItemRequestDtoToItemRequest(itemRequestDto);

        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(description, itemRequest.getDescription());
        assertEquals(created, itemRequest.getCreated());
        assertEquals(user, itemRequest.getRequestor());
    }
}