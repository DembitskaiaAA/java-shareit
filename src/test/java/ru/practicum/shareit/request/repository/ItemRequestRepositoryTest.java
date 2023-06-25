package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void test_findAllByRequestorIdOrderByCreatedDesc() {
        User requester = new User(null, "John Doe", "john@email.com");
        User savedRequester = entityManager.persist(requester);
        LocalDateTime now = LocalDateTime.of(2023, 6, 26, 1, 9);

        ItemRequest request1 = new ItemRequest(null, "Request 1", savedRequester, now.minusDays(2));
        ItemRequest request2 = new ItemRequest(null, "Request 2", savedRequester, now.minusDays(1));
        ItemRequest request3 = new ItemRequest(null, "Request 3", savedRequester, now);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(request3);

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