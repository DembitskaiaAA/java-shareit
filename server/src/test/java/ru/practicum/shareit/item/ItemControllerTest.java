package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    public void testCreateItem() throws Exception {
        Long ownerId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Item Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(123L);

        ItemDto createdItemDto = new ItemDto();
        createdItemDto.setId(1L);
        createdItemDto.setName(itemDto.getName());
        createdItemDto.setDescription(itemDto.getDescription());
        createdItemDto.setAvailable(itemDto.getAvailable());
        createdItemDto.setRequestId(itemDto.getRequestId());

        given(itemService.createItem(ownerId, itemDto)).willReturn(createdItemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdItemDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(createdItemDto.getName())))
                .andExpect(jsonPath("$.description", is(createdItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(createdItemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(createdItemDto.getRequestId().intValue())));
    }

    @Test
    public void testUpdateItem() throws Exception {
        Long ownerId = 1L;
        Long itemId = 1L;

        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setName("Updated Test Item");
        updatedItemDto.setDescription("Updated Test Item Description");
        updatedItemDto.setAvailable(false);
        updatedItemDto.setRequestId(456L);

        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setId(itemId);
        expectedItemDto.setName(updatedItemDto.getName());
        expectedItemDto.setDescription(updatedItemDto.getDescription());
        expectedItemDto.setAvailable(updatedItemDto.getAvailable());
        expectedItemDto.setRequestId(updatedItemDto.getRequestId());

        given(itemService.updateItem(ownerId, itemId, updatedItemDto)).willReturn(expectedItemDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedItemDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(expectedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(expectedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(expectedItemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(expectedItemDto.getRequestId().intValue())));
    }

    @Test
    public void testGetItem() throws Exception {
        Long ownerId = 1L;
        Long itemId = 1L;

        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setId(itemId);
        expectedItemDto.setName("Test Item");
        expectedItemDto.setDescription("Test Item Description");
        expectedItemDto.setAvailable(true);
        expectedItemDto.setRequestId(123L);

        given(itemService.getItem(itemId, ownerId)).willReturn(expectedItemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedItemDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(expectedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(expectedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(expectedItemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(expectedItemDto.getRequestId().intValue())));
    }

    @Test
    public void testGetItems() throws Exception {
        Long ownerId = 1L;
        Integer from = 0;
        Integer size = 10;

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("Test Item 1");
        itemDto1.setDescription("Test Item Description 1");
        itemDto1.setAvailable(true);
        itemDto1.setRequestId(123L);

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("Test Item 2");
        itemDto2.setDescription("Test Item Description 2");
        itemDto2.setAvailable(false);
        itemDto2.setRequestId(456L);

        List<ItemDto> expectedItemList = Arrays.asList(itemDto1, itemDto2);

        given(itemService.getItems(ownerId, from, size)).willReturn(expectedItemList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto1.getRequestId().intValue())))
                .andExpect(jsonPath("$[1].id", is(itemDto2.getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDto2.getAvailable())))
                .andExpect(jsonPath("$[1].requestId", is(itemDto2.getRequestId().intValue())));
    }

    @Test
    public void testSearchItems() throws Exception {
        Integer from = 0;
        Integer size = 10;

        String searchText = "test";

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("Test Item 1");
        itemDto1.setDescription("Test Item Description 1");
        itemDto1.setAvailable(true);
        itemDto1.setRequestId(123L);

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("Test Item 2");
        itemDto2.setDescription("Another Test Item Description");
        itemDto2.setAvailable(false);
        itemDto2.setRequestId(456L);

        List<ItemDto> expectedItemList = Arrays.asList(itemDto1);

        given(itemService.searchItems(searchText, from, size)).willReturn(expectedItemList);

        mockMvc.perform(get("/items/search")
                        .param("text", searchText)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto1.getRequestId().intValue())));
    }

    @Test
    public void createComment_ReturnsCommentDto() throws Exception {
        Long userId = 1L;
        Long itemId = 2L;
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        CommentDto commentDto = new CommentDto(null, "test comment", null, "John Doe",
                LocalDateTime.now());
        CommentDto expectedCommentDto = new CommentDto(1L, "test comment", null, "John Doe",
                LocalDateTime.now());
        given(itemService.createComment(commentDto, itemId, userId)).willReturn(expectedCommentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedCommentDto.getId()))
                .andExpect(jsonPath("$.text").value(expectedCommentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(expectedCommentDto.getAuthorName()));

        verify(itemService, times(1)).createComment(commentDto, itemId, userId);
        verifyNoMoreInteractions(itemService);
    }
}