package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void test_findAllByRequestorIdOrderByCreatedDesc() {
        User requester = new User(1L, "John Doe", "john@email.com");
        userRepository.save(requester);
        LocalDateTime now = LocalDateTime.now();

        ItemRequest request1 = new ItemRequest(1L, "Request 1", requester, now.minusDays(2));
        ItemRequest request2 = new ItemRequest(2L, "Request 2", requester, now.minusDays(1));
        ItemRequest request3 = new ItemRequest(3L, "Request 3", requester, now);

        itemRequestRepository.saveAll(Arrays.asList(request1, request2, request3));

        Long requestorId = 1L;
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(requestorId);

        assertEquals(3, requests.size());

        assertTrue(requests.get(0).getCreated().isAfter(requests.get(1).getCreated()));
        assertTrue(requests.get(1).getCreated().isAfter(requests.get(2).getCreated()));

        for (ItemRequest request : requests) {
            assertEquals(requestorId, request.getRequestor().getId());
        }
    }
}