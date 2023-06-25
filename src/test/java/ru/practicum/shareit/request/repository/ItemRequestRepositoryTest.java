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
        entityManager.persist(requester);
        LocalDateTime now = LocalDateTime.now();

        ItemRequest request1 = new ItemRequest(null, "Request 1", requester, now.minusDays(2));
        ItemRequest request2 = new ItemRequest(null, "Request 2", requester, now.minusDays(1));
        ItemRequest request3 = new ItemRequest(null, "Request 3", requester, now);
        entityManager.merge(request1);
        entityManager.merge(request2);
        entityManager.merge(request3);
        entityManager.flush();

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