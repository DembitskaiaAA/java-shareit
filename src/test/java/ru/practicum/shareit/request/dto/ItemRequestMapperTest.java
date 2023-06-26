package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    public void testTransformItemRequestToItemRequestDto() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(1L, "John Doe", "john@email.com");
        ItemRequest itemRequest = new ItemRequest(1L, "item", user, now);
        ItemRequestDto expectedDto = new ItemRequestDto(1L, "item", 1L, now, new ArrayList<>());

        when(itemService.getItemsByRequestId(1L)).thenReturn(new ArrayList<>());

        ItemRequestDto resultDto = itemRequestMapper.transformItemRequestToItemRequestDto(itemRequest);

        assertEquals(expectedDto.getId(), resultDto.getId());
        assertEquals(expectedDto.getDescription(), resultDto.getDescription());
        assertEquals(expectedDto.getCreated(), resultDto.getCreated());
        assertNull(resultDto.getRequestor());
    }


}