package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Service
public interface ItemRequestService {
    ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getRequests(Long userId);

    ItemRequestDto getRequestById(Long userId, Long requestId);

    ItemRequest validItemRequest(Long requestId);

    List<ItemRequestDto> getRequestsByPage(Long userId, Integer from, Integer size);
}
