package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(Long ownerId, ItemDto itemDto) {
        return post("", ownerId, null, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long ownerId, long itemId, ItemDto itemDto) {
        return patch("/" + itemId, ownerId, null, itemDto);
    }

    public ResponseEntity<Object> getItem(Long itemId, Long ownerId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> getItems(Long ownerId, Integer from, Integer size) {
        Map<String, Object> params = Map.of("from", from, "size", size);
        return get("?from={from}&size={size}", ownerId, params);
    }

    public ResponseEntity<Object> searchItems(String text, Integer from, Integer size) {
        Long userId = 1L;
        Map<String, Object> params = Map.of("text", text, "from", from, "size", size);
        return get("/search?text={text}&from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> createComment(CommentDto commentDto, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, null, commentDto);
    }
}
