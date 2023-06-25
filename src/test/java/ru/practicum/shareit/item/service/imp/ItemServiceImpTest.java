package ru.practicum.shareit.item.service.imp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImpTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingService bookingService;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemMapperImpl itemMapperImpl;
    @Mock
    private ItemRequestService itemRequestService;
    @Captor
    private ArgumentCaptor<Item> itemCaptor;
    @Captor
    private ArgumentCaptor<Comment> commentCaptor;
    @Mock
    private ItemServiceImp itemService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        itemService = new ItemServiceImp(itemRepository, userService, bookingService,
                commentRepository, itemMapperImpl, itemRequestService);
    }

    @Test
    public void testCreateItem() {
        Long ownerId = 1L;
        Long requestId = 2L;
        Long itemId = 1L;
        String itemName = "testItem";
        String itemDescription = "testDescription";
        Boolean available = true;
        List<CommentDto> commentDtos = new ArrayList<>();
        LocalDateTime created = LocalDateTime.now();

        User owner = new User(ownerId, "owner", "owner@email.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "test", owner, created);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName(itemName);
        itemDto.setDescription(itemDescription);
        itemDto.setAvailable(available);
        itemDto.setRequestId(requestId);
        itemDto.setComments(commentDtos);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);

        Item savedItem = new Item(1L, itemName, itemDescription, available, owner, itemRequest);
        ItemDto expected = new ItemDto();
        expected.setId(savedItem.getId());
        expected.setName(savedItem.getName());
        expected.setDescription(savedItem.getDescription());
        expected.setAvailable(savedItem.getAvailable());
        expected.setRequestId(requestId);
        expected.setComments(new ArrayList<>());
        expected.setLastBooking(null);
        expected.setNextBooking(null);

        when(userService.validUser(ownerId)).thenReturn(owner);
        when(itemRequestService.validItemRequest(requestId)).thenReturn(itemRequest);
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        ItemDto result = itemService.createItem(ownerId, itemDto);

        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getAvailable(), result.getAvailable());
        assertEquals(expected.getDescription(), result.getDescription());
        assertEquals(expected.getRequestId(), result.getRequestId());
    }

    @Test
    public void testUpdateItemSuccess() {
        Long ownerId = 1L;
        Long requestId = 2L;
        String itemName = "testItem";
        String itemDescription = "testDescription";
        Boolean available = true;
        LocalDateTime created = LocalDateTime.now();

        User owner = new User(ownerId, "owner", "owner@email.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "test", owner, created);

        Item item = new Item();
        item.setName(itemName);
        item.setDescription(itemDescription);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(itemRequest);

        // Создаем тестовый объект ItemDto для обновления товара
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Новое имя товара");
        itemDto.setDescription("Новое описание товара");
        itemDto.setAvailable(false);
        itemDto.setRequestId(2L);

        // Задаем поведение моков зависимостей для методов validItem и validUser
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.validUser(1L)).thenReturn(owner);

        // Вызываем тестируемый метод updateItem
        itemService.updateItem(1L, 1L, itemDto);

        verify(itemRepository).save(itemCaptor.capture());
        Item savedItem = itemCaptor.getValue();

        // Проверяем корректность обновленных полей товара
        assertEquals("Новое имя товара", savedItem.getName());
        assertEquals("Новое описание товара", savedItem.getDescription());
        assertFalse(savedItem.getAvailable());
        assertNotNull(savedItem.getRequest());

        // Проверяем, что методы validItem и validUser были вызваны один раз соответственно с аргументами itemId и ownerId
        verify(itemRepository, times(1)).findById(1L);
        verify(userService, times(1)).validUser(1L);
    }

    @Test
    void testGetItemReturnsCorrectDtoForDifferentOwners() {
        Long itemId = 1L;
        Long ownerId = 2L;
        User owner = new User(ownerId, "owner", "owner@email.com");

        Item item = new Item(itemId, "name", "description", true, owner, null);
        ItemDto expectedDtoForNonOwner = new ItemDto();
        expectedDtoForNonOwner.setId(itemId);
        expectedDtoForNonOwner.setName("name");
        expectedDtoForNonOwner.setDescription("description");
        expectedDtoForNonOwner.setAvailable(true);
        expectedDtoForNonOwner.setComments(new ArrayList<>());
        expectedDtoForNonOwner.setLastBooking(null);
        expectedDtoForNonOwner.setNextBooking(null);

        ItemDto expectedDtoForOwner = new ItemDto();
        expectedDtoForOwner.setId(itemId);
        expectedDtoForOwner.setName("name");
        expectedDtoForOwner.setDescription("description");
        expectedDtoForOwner.setAvailable(true);
        expectedDtoForOwner.setComments(new ArrayList<>());
        expectedDtoForOwner.setLastBooking(null);
        expectedDtoForOwner.setNextBooking(null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto actualDtoForNonOwner = itemService.getItem(itemId, ownerId + 1);
        ItemDto actualDtoForOwner = itemService.getItem(itemId, ownerId);

        assertEquals(expectedDtoForNonOwner, actualDtoForNonOwner);
        assertEquals(expectedDtoForOwner, actualDtoForOwner);
    }

    @Test
    void testGetItemThrowsExceptionIfItemNotFound() {
        Long itemId = 1L;
        Long ownerId = 2L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(itemId, ownerId));
    }

    @Test
    public void testGetItemsWithEmptyItemList() {
// Arrange
        Long ownerId = 1L;
        Integer from = 0;
        Integer size = 10;
        Page<Item> page = new PageImpl<>(new ArrayList<>());
        when(itemRepository.findAllByOwnerId(ownerId, PageRequest.of(0, size, Sort.by("id").ascending()))).thenReturn(page);

        // Act
        List<ItemDto> actualItemList = itemService.getItems(ownerId, from, size);

        // Assert
        assertNotNull(actualItemList);
        assertEquals(0, actualItemList.size());
    }

    @Test
    public void testGetItemsWithNonEmptyItemList() {
// Arrange
        Long ownerId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<Item> mockItemList = new ArrayList<>();
        User owner = new User();
        owner.setId(ownerId);
        for (int i = 1; i <= 20; i++) {
            Item item = new Item();
            item.setId((long) i);
            item.setName("Item " + i);
            item.setDescription("Description " + i);
            item.setAvailable(i % 2 == 0);
            item.setOwner(owner);
            mockItemList.add(item);
        }
        Page<Item> page = new PageImpl<>(mockItemList);
        when(itemRepository.findAllByOwnerId(ownerId, PageRequest.of(0, size, Sort.by("id").ascending()))).thenReturn(page);
        List<ItemDto> expectedItemList = mockItemList.subList(from, Math.min((from + size), mockItemList.size()))
                .stream().map(itemMapperImpl::transformItemToItemForOwnerDto).collect(toList());

        // Act
        List<ItemDto> actualItemList = itemService.getItems(ownerId, from, size);

        // Assert
        assertNotNull(actualItemList);
        assertEquals(expectedItemList.size(), actualItemList.size());
        assertTrue(expectedItemList.containsAll(actualItemList));
    }

    @Test
    public void testGetItemsWithInvalidOwnerId() {
// Arrange
        Long ownerId = -1L;
        Integer from = 0;
        Integer size = 10;
        when(userService.validUser(anyLong())).thenThrow(new NotFoundException("Invalid ownerId"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> itemService.getItems(ownerId, from, size));
    }

    @Test
    public void testGetItemsWithInvalidSize() {
// Arrange
        Long ownerId = 1L;
        Integer from = 0;
        Integer size = null;

        // Act
        List<ItemDto> actualItemList = itemService.getItems(ownerId, from, size);

        // Assert
        assertNotNull(actualItemList);
        assertEquals(0, actualItemList.size());
    }


    @Test
    public void testSearchItemsWithValidTextAndSize() {
        // Arrange
        String text = "test";
        int from = 0;
        int size = 1;
        Page<Item> page = new PageImpl<>(List.of(new Item()));
        when(itemRepository.search(anyString(), any(Pageable.class))).thenReturn(page);

        // Act
        List<ItemDto> result = itemService.searchItems(text, from, size);

        // Assert
        assertNotNull(result);
        assertEquals(size, result.size());
        verify(itemRepository, times(1)).search(eq(text), any(Pageable.class));
    }

    @Test
    public void testSearchItemsWithBlankText() {
        // Arrange
        String text = "";
        int from = 0;
        int size = 1;

        // Act
        List<ItemDto> result = itemService.searchItems(text, from, size);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).search(anyString(), any(Pageable.class));
    }

    @Test
    public void testSearchItemsWithNullSize() {
        // Arrange
        String text = "test";
        int from = 0;
        Integer size = null;

        // Act
        List<ItemDto> result = itemService.searchItems(text, from, size);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).search(anyString(), any(Pageable.class));
    }


    @Test
    public void testCreateCommentForBookedItem() {
        Long itemId = 1L;
        Long userId = 2L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test comment");

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);

        when(userService.validUser(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingService.getBookingByItemIdBookerIdForComment(itemId, userId)).thenReturn(booking);

        itemService.createComment(commentDto, itemId, userId);
        verify(commentRepository).save(commentCaptor.capture());
        Comment comment = commentCaptor.getValue();

        assertEquals(commentDto.getText(), comment.getText());
    }

    @Test
    public void testCreateCommentWithInvalidItemId() {
        Long itemId = 1L;
        Long userId = 2L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test comment");

        when(userService.validUser(userId)).thenReturn(new User());
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotAvailableException.class, () -> itemService.createComment(commentDto, itemId, userId));
    }

    @Test
    public void testCreateCommentWithoutBooking() {
        Long itemId = 1L;
        Long userId = 2L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test comment");

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        when(userService.validUser(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingService.getBookingByItemIdBookerIdForComment(itemId, userId)).thenReturn(null);

        assertThrows(NotAvailableException.class, () -> itemService.createComment(commentDto, itemId, userId));
    }

    @Test
    public void testCreateCommentWithCommentData() {
        // given
        Long itemId = 1L;
        Long userId = 2L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test comment");

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        when(userService.validUser(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingService.getBookingByItemIdBookerIdForComment(itemId, userId)).thenReturn(booking);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);


        // when
        CommentDto createdComment = itemService.createComment(commentDto, itemId, userId);

        // then
        assertNotNull(createdComment.getId());
        assertEquals(commentDto.getText(), createdComment.getText());
        assertEquals(item, createdComment.getItem());
        assertEquals(user.getName(), createdComment.getAuthorName());
        assertEquals(comment.getCreated(), createdComment.getCreated());
    }

    @Test
    public void testGetCommentsByItemId() {
        Long itemId = 1L;

        // Создаем список комментариев
        List<Comment> comments = new ArrayList<>();
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("Comment 1");
        comment1.setAuthor(new User());
        comment1.setCreated(LocalDateTime.now().minusDays(1));
        comments.add(comment1);

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setText("Comment 2");
        comment2.setAuthor(new User());
        comment2.setCreated(LocalDateTime.now());
        comments.add(comment2);

        // Настраиваем мок-объект commentRepository
        when(commentRepository.findAllByItemId(eq(itemId), any())).thenReturn(comments);

        // Вызываем тестируемый метод
        List<CommentDto> result = itemService.getCommentsByItemId(itemId);

        // Проверяем результат
        assertEquals(2, result.size());
        assertEquals(comment2.getId(), result.get(1).getId());
        assertEquals(comment1.getId(), result.get(0).getId()); // изменено на comment1.getId()
    }

    @Test
    public void testGetItemsByRequestId() {
        Long requestId = 1L;

        // Создаем список предметов
        List<Item> items = new ArrayList<>();
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        items.add(item1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        items.add(item2);

        // Настраиваем мок-объект itemRepository
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(items);

        // Вызываем тестируемый метод
        List<ItemDto> result = itemService.getItemsByRequestId(requestId);

        // Проверяем результат
        assertEquals(2, result.size());
        assertEquals(item1.getId(), result.get(0).getId());
        assertEquals(item2.getId(), result.get(1).getId());
    }

}