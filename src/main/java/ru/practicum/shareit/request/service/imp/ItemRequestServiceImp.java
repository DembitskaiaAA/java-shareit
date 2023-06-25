package ru.practicum.shareit.request.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImp implements ItemRequestService {

    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;


    @Autowired
    public ItemRequestServiceImp(ItemRequestMapper itemRequestMapper, ItemRequestRepository itemRequestRepository, UserService userService) {
        this.itemRequestMapper = itemRequestMapper;
        this.itemRequestRepository = itemRequestRepository;
        this.userService = userService;

    }

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        userService.validUser(userId);
        itemRequestDto.setRequestor(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestMapper.transformItemRequestDtoToItemRequest(itemRequestDto);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.transformItemRequestToItemRequestDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestDto> getRequests(Long userId) {
        userService.validUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return itemRequests.stream()
                .map(itemRequestMapper::transformItemRequestToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userService.validUser(userId);
        ItemRequest itemRequest = validItemRequest(requestId);
        return itemRequestMapper.transformItemRequestToItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getRequestsByPage(Long userId, Integer from, Integer size) {
        if (size == null) {
            return new ArrayList<>();
        }
        Pageable pageable = PageRequest.of(0, size, Sort.by("created").ascending());
        List<ItemRequest> result = itemRequestRepository.findAll(pageable).getContent();
        List<ItemRequest> requests = result.subList(from, Math.min((from + size), result.size()));
        return requests.stream().filter(x -> x.getRequestor().getId() != userId)
                .map(itemRequestMapper::transformItemRequestToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequest validItemRequest(Long requestId) {
        if (requestId == null) {
            return null;
        }
        return itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(
                String.format("Запрос с id: %s отсутствует", requestId)));
    }


}
