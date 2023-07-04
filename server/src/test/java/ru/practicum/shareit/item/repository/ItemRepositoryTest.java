package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testSearch() {
        User user = new User(null, "user", "password");
        User savedUser = entityManager.persist(user);

        Item item1 = new Item(null, "item1", "description1", true, savedUser, null);
        Item item2 = new Item(null, "item2", "description2", true, savedUser, null);
        Item item3 = new Item(null, "item3", "description3", false, savedUser, null);
        Item savedItem1 = entityManager.merge(item1);
        Item savedItem2 = entityManager.merge(item2);
        Item savedItem3 = entityManager.merge(item3);
        entityManager.flush();

        Page<Item> result = itemRepository.search("item", PageRequest.of(0, 10));
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().contains(savedItem1));
        assertTrue(result.getContent().contains(savedItem2));
        assertFalse(result.getContent().contains(savedItem3));
    }

    @Test
    void testFindByOwnerId() {
        User user = new User(null, "user", "password");
        User savedUser = entityManager.persist(user);

        Item item1 = new Item(null, "item1", "description1", true, savedUser, null);
        Item item2 = new Item(null, "item2", "description2", true, savedUser, null);
        Item item3 = new Item(null, "item3", "description3", false, savedUser, null);
        Item savedItem1 = entityManager.persist(item1);
        Item savedItem2 = entityManager.persist(item2);
        Item savedItem3 = entityManager.persist(item3);

        Page<Item> result = itemRepository.findAllByOwnerId(user.getId(), PageRequest.of(0, 10));

        assertEquals(3, result.getTotalElements());
        assertTrue(result.get().anyMatch(i -> i.equals(savedItem1)));
        assertTrue(result.get().anyMatch(i -> i.equals(savedItem2)));
        assertTrue(result.get().anyMatch(i -> i.equals(savedItem3)));
    }

    @Test
    void testFindAllByRequestId() {
        User user = new User(null, "user", "password");
        User savedUser = entityManager.persist(user);

        ItemRequest request = new ItemRequest(null, "request1", savedUser, LocalDateTime.now());
        entityManager.persist(request);

        Item item1 = new Item(null, "item1", "description1", true, user, request);
        Item item2 = new Item(null, "item2", "description2", false, user, request);
        Item item3 = new Item(null, "item3", "description3", true, user, null);
        Item savedItem1 = entityManager.persist(item1);
        Item savedItem2 = entityManager.persist(item2);
        Item savedItem3 = entityManager.persist(item3);

        List<Item> result = itemRepository.findAllByRequestId(request.getId());

        assertEquals(2, result.size());
        assertTrue(result.contains(savedItem1));
        assertTrue(result.contains(savedItem2));
        assertFalse(result.contains(savedItem3));
    }
}