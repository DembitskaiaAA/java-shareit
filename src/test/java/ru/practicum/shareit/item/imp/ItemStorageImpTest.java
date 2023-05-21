package ru.practicum.shareit.item.imp;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.imp.UserStorageImp;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemStorageImpTest {
    private UserStorageImp userStorage;
    private ItemStorageImp itemStorageImp;

    @Before
    @Autowired
    public void setUp() {
        userStorage = new UserStorageImp();
        itemStorageImp = new ItemStorageImp(userStorage);
    }

    @Test
    public void testCreateItem() {
        User user1 = new User();
        user1.setName("Иван");
        user1.setEmail("ivan@example.com");

        UserDto resultDto1 = userStorage.createUser(user1);

        String itemDtoName = "Товар 1";
        String itemDtoDescription = "Описание товара 1";
        Boolean itemDtoAvailable = true;
        ItemDto createdItemDto = itemStorageImp.createItem(resultDto1.getId(), new ItemDto(0L, "Товар 1", "Описание товара 1", true));


        assertNotNull(createdItemDto.getId());
        assertEquals(itemDtoName, createdItemDto.getName());
        assertEquals(itemDtoDescription, createdItemDto.getDescription());
        assertEquals(itemDtoAvailable, createdItemDto.getAvailable());
    }

    @Test
    public void testUpdateItem() {
        User user = new User();
        user.setName("Иван");
        user.setEmail("ivan@example.com");

        UserDto resultDto1 = userStorage.createUser(user);

        ItemDto createdItemDto = itemStorageImp.createItem(resultDto1.getId(), new ItemDto(0L, "Товар 1", "Описание товара 1", true));


        ItemDto updatedItem = itemStorageImp.updateItem(user.getId(), createdItemDto.getId(),
                new Item("updated name", "updated description", false, user.getId()));
        assertNotNull(updatedItem);
        assertEquals(createdItemDto.getId(), updatedItem.getId());
        assertEquals("updated name", updatedItem.getName());
        assertEquals("updated description", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());

        try {
            itemStorageImp.updateItem(user.getId(), 12345L,
                    new Item("updated name", "updated description", false, user.getId()));
            fail("Expected NotFoundException but no exception was thrown");
        } catch (NotFoundException e) {
            assertTrue(e.getMessage().contains("отсутствует"));
        }
    }

    @Test
    void testGetItem_success() {
        User user = new User();
        user.setName("Иван");
        user.setEmail("ivan@example.com");
        UserDto resultDto1 = userStorage.createUser(user);

        ItemDto createdItemDto = itemStorageImp.createItem(resultDto1.getId(), new ItemDto(0L, "Товар 1", "Описание товара 1", true));

        ItemDto result = itemStorageImp.getItem(createdItemDto.getId());

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Товар 1", result.getName());
        assertEquals("Описание товара 1", result.getDescription());
        assertEquals(true, result.getAvailable());
    }

    @Test
    void testGetItems_success() {
        User user = new User();
        user.setName("Иван");
        user.setEmail("ivan@example.com");
        UserDto resultDto1 = userStorage.createUser(user);

        ItemDto createdItemDto = itemStorageImp.createItem(resultDto1.getId(), new ItemDto(0L, "Товар 1", "Описание товара 1", true));
        ItemDto createdItemDto2 = itemStorageImp.createItem(resultDto1.getId(), new ItemDto(1L, "Товар 2", "Описание товара 2", true));

        List<ItemDto> result = itemStorageImp.getItems(resultDto1.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Товар 1", result.get(0).getName());
        assertEquals("Товар 2", result.get(1).getName());
    }

    @Test
    void testSearchItems_success() {
        User user = new User();
        user.setName("Иван");
        user.setEmail("ivan@example.com");
        UserDto resultDto1 = userStorage.createUser(user);

        ItemDto createdItemDto = itemStorageImp.createItem(resultDto1.getId(), new ItemDto(0L, "Товар 1", "Описание товара 1", true));
        ItemDto createdItemDto2 = itemStorageImp.createItem(resultDto1.getId(), new ItemDto(1L, "Товар 2", "Описание товара 2", true));

        List<ItemDto> result = itemStorageImp.searchItems("ТоВаР");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Товар 1", result.get(0).getName());
        assertEquals("Товар 2", result.get(1).getName());
    }

    @Test
    void testSearchItems_success2() {
        User user = new User();
        user.setName("Иван");
        user.setEmail("ivan@example.com");
        UserDto resultDto1 = userStorage.createUser(user);

        ItemDto createdItemDto = itemStorageImp.createItem(resultDto1.getId(), new ItemDto(0L, "Товар 1", "Описание товара 1", true));
        ItemDto createdItemDto2 = itemStorageImp.createItem(resultDto1.getId(), new ItemDto(1L, "Товар 2", "Описание товара 2", true));

        List<ItemDto> result = itemStorageImp.searchItems("");

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}