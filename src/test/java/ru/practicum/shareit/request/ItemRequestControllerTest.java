package ru.practicum.shareit.request;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    public void createRequest_ReturnsItemRequestDto() throws Exception {
        Long userId = 1L;
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        ItemRequestDto requestDto = new ItemRequestDto(
                1L, "test description", null, LocalDateTime.now(), Collections.emptyList()
        );
        ItemRequestDto expectedResponse = new ItemRequestDto(
                1L, "test description", userId, LocalDateTime.now(), Collections.emptyList()
        );
        given(itemRequestService.createRequest(eq(userId), any(ItemRequestDto.class)))
                .willReturn(expectedResponse);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                .andExpect(jsonPath("$.description").value(expectedResponse.getDescription()));

        verify(itemRequestService, times(1)).createRequest(eq(userId), any(ItemRequestDto.class));
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    public void getRequests_ReturnsListOfItemRequestDto() throws Exception {
        Long userId = 1L;
        List<ItemRequestDto> expectedResponse = Arrays.asList(
                new ItemRequestDto(
                        1L, "test description 1", userId, LocalDateTime.now(), Collections.emptyList()
                ),
                new ItemRequestDto(
                        2L, "test description 2", userId, LocalDateTime.now(), Collections.emptyList()
                )
        );
        given(itemRequestService.getRequests(userId))
                .willReturn(expectedResponse);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(expectedResponse.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(expectedResponse.get(0).getDescription()))
                .andExpect(jsonPath("$[1].id").value(expectedResponse.get(1).getId()))
                .andExpect(jsonPath("$[1].description").value(expectedResponse.get(1).getDescription()));

        verify(itemRequestService, times(1)).getRequests(userId);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    public void getRequestsByPage_ReturnsListOfItemRequestDto() throws Exception {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<ItemRequestDto> expectedResponse = Arrays.asList(
                new ItemRequestDto(
                        1L, "test description 1", userId, LocalDateTime.now(), Collections.emptyList()
                ),
                new ItemRequestDto(
                        2L, "test description 2", userId, LocalDateTime.now(), Collections.emptyList()
                )
        );
        given(itemRequestService.getRequestsByPage(userId, from, size))
                .willReturn(expectedResponse);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(expectedResponse.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(expectedResponse.get(0).getDescription()))
                .andExpect(jsonPath("$[1].id").value(expectedResponse.get(1).getId()))
                .andExpect(jsonPath("$[1].description").value(expectedResponse.get(1).getDescription()));

        verify(itemRequestService, times(1)).getRequestsByPage(userId, from, size);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    public void getRequestById_ReturnsItemRequestDto() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;
        ItemRequestDto expectedResponse = new ItemRequestDto(
                requestId, "test description", userId, LocalDateTime.now(), Collections.emptyList()
        );
        given(itemRequestService.getRequestById(userId, requestId))
                .willReturn(expectedResponse);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                .andExpect(jsonPath("$.description").value(expectedResponse.getDescription()));

        verify(itemRequestService, times(1)).getRequestById(userId, requestId);
        verifyNoMoreInteractions(itemRequestService);
    }
}