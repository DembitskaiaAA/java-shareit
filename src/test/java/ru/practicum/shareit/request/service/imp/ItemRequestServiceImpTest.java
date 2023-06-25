package ru.practicum.shareit.request.service.imp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImpTest {

    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private ItemRequestServiceImp itemRequestService;


    @Test
    public void testCreateRequest() {
        Long userId = 1L;

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test description");
        itemRequestDto.setRequestor(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(new User());
        itemRequest.setCreated(LocalDateTime.now());

        when(userService.validUser(userId)).thenReturn(new User());

        when(itemRequestMapper.transformItemRequestDtoToItemRequest(any(ItemRequestDto.class))).thenReturn(itemRequest);
        when(itemRequestMapper.transformItemRequestToItemRequestDto(any(ItemRequest.class))).thenReturn(itemRequestDto);

        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.createRequest(userId, itemRequestDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(userId, result.getRequestor());
    }

    @Test
    public void testGetRequests_WhenValidUserId_ReturnsListOfItemRequestDtos() {
        Long userId = 1L;
        User user = new User(userId, "John", "Doe");
        ItemRequest itemRequest1 = new ItemRequest(1L, "Description 1", user, LocalDateTime.now().minusHours(1));
        ItemRequest itemRequest2 = new ItemRequest(2L, "Description 2", user, LocalDateTime.now());
        List<ItemRequest> itemRequests = Arrays.asList(itemRequest1, itemRequest2);
        List<ItemRequestDto> expectedItemRequestDtos = Arrays.asList(
                new ItemRequestDto(),
                new ItemRequestDto()
        );

        when(userService.validUser(userId)).thenReturn(user);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId)).thenReturn(itemRequests);
        when(itemRequestMapper.transformItemRequestToItemRequestDto(itemRequest1)).thenReturn(expectedItemRequestDtos.get(0));
        when(itemRequestMapper.transformItemRequestToItemRequestDto(itemRequest2)).thenReturn(expectedItemRequestDtos.get(1));

        List<ItemRequestDto> actualItemRequestDtos = itemRequestService.getRequests(userId);

        assertEquals(expectedItemRequestDtos, actualItemRequestDtos);
        verify(userService).validUser(userId);
        verify(itemRequestRepository).findAllByRequestorIdOrderByCreatedDesc(userId);
        verify(itemRequestMapper, times(2)).transformItemRequestToItemRequestDto(any(ItemRequest.class));
    }

    @Test
    public void testGetRequestsInvalidUser() {
        Long userId = 1L;
        when(userService.validUser(userId)).thenThrow(new NotFoundException("Invalid user"));
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequests(userId));
    }

    @Test
    public void testGetRequestById_WhenValidUserIdAndRequestId_ReturnsItemRequestDto() {
        Long userId = 1L;
        User user = new User(userId, "John", "Doe");
        Long requestId = 1L;
        ItemRequest itemRequest = new ItemRequest(requestId, "Description", user, LocalDateTime.now());
        ItemRequestDto expectedItemRequestDto = new ItemRequestDto();

        when(userService.validUser(userId)).thenReturn(user);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.transformItemRequestToItemRequestDto(itemRequest)).thenReturn(expectedItemRequestDto);

        ItemRequestDto actualItemRequestDto = itemRequestService.getRequestById(userId, requestId);

        assertEquals(expectedItemRequestDto, actualItemRequestDto);
        verify(userService).validUser(userId);
        verify(itemRequestRepository).findById(requestId);
        verify(itemRequestMapper).transformItemRequestToItemRequestDto(itemRequest);
    }

    @Test
    public void testGetRequestById_WhenInvalidUserId_ThrowsInvalidUserException() {
        Long invalidUserId = -1L;
        Long requestId = 1L;

        when(userService.validUser(invalidUserId)).thenThrow(new NotFoundException("Invalid user id"));

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(invalidUserId, requestId);
        });
    }

    @Test
    public void testGetRequestById_WhenInvalidRequestId_ThrowsEntityNotFoundException() {
        Long userId = 1L;
        Long invalidRequestId = -1L;

        when(userService.validUser(userId)).thenReturn(new User(userId, "John", "Doe"));
        when(itemRequestRepository.findById(invalidRequestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(userId, invalidRequestId);
        });
    }

    @Test
    public void testGetRequestsByPage_WhenSizeIsNull_ReturnsEmptyList() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = null;

        List<ItemRequestDto> actualItemRequestDtos = itemRequestService.getRequestsByPage(userId, from, size);

        assertTrue(actualItemRequestDtos.isEmpty());
    }

    @Test
    public void testGetRequestsByPage_WhenValidUserIdAndSize_ReturnsFilteredItemRequestDtoList() {
        Long userId = 1L;
        Long userId2 = 2L;
        User user = new User(userId, "John", "Doe");
        Integer from = 0;
        Integer size = 2;
        LocalDateTime now = LocalDateTime.now();
        List<ItemRequest> itemRequests = Arrays.asList(
                new ItemRequest(1L, "Description 1", user, now),
                new ItemRequest(2L, "Description 2", user, now.plusMinutes(1)),
                new ItemRequest(3L, "Description 3", user, now.plusMinutes(2))
        );
        List<ItemRequestDto> expectedItemRequestDtos = Arrays.asList(
                new ItemRequestDto(),
                new ItemRequestDto()
        );

        when(itemRequestRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(itemRequests));
        when(itemRequestMapper.transformItemRequestToItemRequestDto(isA(ItemRequest.class)))
                .thenAnswer(invocation -> {
                    ItemRequest itemRequest = invocation.getArgument(0);
                    return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getRequestor().getId(), itemRequest.getCreated(), new ArrayList<>());
                });

        List<ItemRequestDto> actualItemRequestDtos = itemRequestService.getRequestsByPage(userId2, from, size);

        assertEquals(expectedItemRequestDtos.size(), actualItemRequestDtos.size());
        verify(itemRequestRepository).findAll(any(Pageable.class));
        verify(itemRequestMapper, times(2)).transformItemRequestToItemRequestDto(any(ItemRequest.class));
    }
}