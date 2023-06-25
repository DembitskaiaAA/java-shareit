package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public abstract class ItemRequestMapper {
    @Autowired
    protected UserService userService;
    @Autowired
    protected ItemService itemService;

    @Mapping(target = "requestor", expression = "java(mapToRequestor(itemRequestDto.getRequestor(), userService))")
    public abstract ItemRequest transformItemRequestDtoToItemRequest(ItemRequestDto itemRequestDto);

    @Mapping(target = "requestor", ignore = true)
    @Mapping(target = "items", expression = "java(mapToItems(itemRequest.getId(), itemService))")
    public abstract ItemRequestDto transformItemRequestToItemRequestDto(ItemRequest itemRequest);

    List<ItemDto> mapToItems(Long itemRequestId, ItemService itemService) {
        return itemService.getItemsByRequestId(itemRequestId);
    }

    User mapToRequestor(Long userId, UserService userService) {
        return userService.getUser(userId);
    }

    Long mapToRequestorId(User user) {
        return user.getId();
    }

}
