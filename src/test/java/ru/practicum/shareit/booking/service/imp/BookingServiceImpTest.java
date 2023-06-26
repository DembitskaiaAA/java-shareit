package ru.practicum.shareit.booking.service.imp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingStateException;
import ru.practicum.shareit.exceptions.BookingTimeException;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBookerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserBookerDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImpTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @InjectMocks
    private BookingServiceImp bookingService;


    @Test
    public void testCreateBookingSuccess() {
        Long owner = 1L;
        Long bookerId = 2L;
        Long itemId = 2L;
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        ItemRequest itemRequest = new ItemRequest();
        User userOwner = new User(owner, "name", "name@email.com");
        User userBooker = new User(bookerId, "name2", "name2@email.com");
        Item item = new Item(itemId, "Test item", "test", true, userOwner, itemRequest);
        BookingInputDto bookingInputDto = new BookingInputDto(start, end, itemId);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(userBooker);

        when(userService.validUser(bookerId)).thenReturn(userBooker);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.transformBookingInputDtoToBooking(bookingInputDto)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.transformBookingToBookingOutputDto(booking)).thenReturn(new BookingOutputDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new UserBookerDto(bookerId),
                new ItemBookerDto(itemId, item.getName())
        ));

        BookingOutputDto result = bookingService.createBooking(bookerId, bookingInputDto);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(bookerId, result.getBooker().getId());
        assertEquals(itemId, result.getItem().getId());
        assertEquals(item.getName(), result.getItem().getName());
    }

    @Test
    public void testCreateBookingWithInvalidTime() {
        BookingRepository bookingRepository = mock(BookingRepository.class);
        UserService userService = mock(UserService.class);
        ItemRepository itemRepository = mock(ItemRepository.class);
        BookingMapper bookingMapper = mock(BookingMapper.class);

        BookingServiceImp bookingService = new BookingServiceImp(
                bookingRepository,
                userService,
                itemRepository,
                bookingMapper
        );

        Long bookerId = 1L;
        Long itemId = 2L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusHours(1);
        BookingInputDto bookingInputDto = new BookingInputDto(start, end, itemId);

        assertThrows(BookingTimeException.class, () -> bookingService.createBooking(bookerId, bookingInputDto));
    }

    @Test
    public void testCreateBookingWithNonExistingUser() {
        Long nonExistingUserId = 99999999L;
        Long itemId = 2L;
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingInputDto bookingInputDto = new BookingInputDto(start, end, itemId);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(nonExistingUserId, bookingInputDto));
    }

    @Test
    void approveBooking_whenOwnerIsNotValid_throwNotFoundException() {
        Long owner = 1L;
        Long bookingId = 2L;
        Boolean approved = true;
        when(userService.validUser(owner)).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(owner, bookingId, approved));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approveBooking_whenBookingIsNotValid_throwNotFoundException() {
        Long owner = 1L;
        Long bookingId = 2L;
        Boolean approved = true;
        User user = new User(owner, "name", "name@email.com");
        when(userService.validUser(owner)).thenReturn(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(owner, bookingId, approved));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approveBooking_whenOwnerIsNotTheSameAsItemOwner_throwNotFoundException() {
        Long owner = 1L;
        Long bookingId = 2L;
        Boolean approved = true;
        User itemOwner = new User();
        itemOwner.setId(3L);
        User booker = new User();
        booker.setId(4L);
        Item item = new Item();
        item.setId(5L);
        item.setOwner(itemOwner);
        Booking savedBooking = new Booking();
        savedBooking.setItem(item);
        savedBooking.setBooker(booker);
        User user = new User(owner, "name", "name@email.com");
        when(userService.validUser(owner)).thenReturn(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(savedBooking));

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(owner, bookingId, approved));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approveBooking_whenBookingIsAlreadyApproved_throwNotAvailableException() {
        Long owner = 1L;
        Long bookingId = 2L;
        Boolean approved = true;
        User itemOwner = new User();
        itemOwner.setId(owner);
        User booker = new User();
        booker.setId(4L);
        Item item = new Item();
        item.setId(5L);
        item.setOwner(itemOwner);
        User user = new User(owner, "name", "name@email.com");
        Booking savedBooking = new Booking();
        savedBooking.setItem(item);
        savedBooking.setBooker(booker);
        savedBooking.setStatus(BookingStatus.APPROVED);
        when(userService.validUser(owner)).thenReturn(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(savedBooking));

        assertThrows(NotAvailableException.class, () -> bookingService.approveBooking(owner, bookingId, approved));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approveBooking_whenApprovedIsTrue_approveBookingAndReturnBookingOutputDto() {
        Long owner = 1L;
        Long bookingId = 2L;
        Boolean approved = true;

        User itemOwner = new User();
        itemOwner.setId(owner);

        User booker = new User();
        booker.setId(4L);

        Item item = new Item();
        item.setId(5L);
        item.setOwner(itemOwner);

        Booking savedBooking = new Booking();
        savedBooking.setItem(item);
        savedBooking.setBooker(booker);
        savedBooking.setStatus(BookingStatus.WAITING);

        Booking resultBooking = new Booking();
        resultBooking.setId(6L);
        resultBooking.setItem(item);
        resultBooking.setBooker(booker);
        resultBooking.setStatus(BookingStatus.APPROVED);

        when(userService.validUser(owner)).thenReturn(itemOwner);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(savedBooking));
        when(bookingRepository.save(any())).thenReturn(resultBooking);
        when(bookingMapper.transformBookingToBookingOutputDto(resultBooking)).thenReturn(new BookingOutputDto());

        BookingOutputDto outputDto = bookingService.approveBooking(owner, bookingId, approved);

        assertNotNull(outputDto);
        assertEquals(BookingStatus.APPROVED, resultBooking.getStatus());
        verify(bookingRepository).save(savedBooking);
    }

    @Test
    public void testGetBookingWithInvalidUser() {
        Long owner = 1L;
        Long bookingId = 2L;
        User booker = new User();
        booker.setId(3L);
        Item item = new Item();
        item.setId(4L);
        item.setOwner(new User());
        item.getOwner().setId(5L);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(item);

        when(userService.validUser(owner)).thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(owner, bookingId));
    }

    @Test
    public void testGetBookingWithInvalidBookingId() {
        Long owner = 1L;
        Long bookingId = 2L;

        when(userService.validUser(owner)).thenReturn(null);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(owner, bookingId));
    }

    @Test
    public void testGetBookingWithOwnerAsItemOwner() {
        Long owner = 1L;
        Long bookingId = 2L;
        User ownerUser = new User();
        ownerUser.setId(owner);
        Item item = new Item();
        item.setId(4L);
        item.setOwner(ownerUser);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(new User());
        booking.setItem(item);

        when(userService.validUser(owner)).thenReturn(null);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.transformBookingToBookingOutputDto(booking)).thenReturn(new BookingOutputDto(booking.getId(), booking.getStart(),
                booking.getEnd(), booking.getStatus(), new UserBookerDto(booking.getBooker().getId()), new ItemBookerDto(booking.getItem().getId(),
                booking.getItem().getName())));

        BookingOutputDto result = bookingService.getBooking(owner, bookingId);

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(booking.getBooker().getId(), result.getBooker().getId());
        assertEquals(booking.getItem().getId(), result.getItem().getId());
        assertEquals(booking.getItem().getName(), result.getItem().getName());
    }

    @Test
    public void testGetBookingFailByNotOwnerOrAsBooker() {

        Long owner = 1L;
        Long bookingId = 2L;

        User ownerUser = new User();
        ownerUser.setId(3L);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(4L);
        item.setOwner(ownerUser);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(item);

        when(userService.validUser(anyLong())).thenReturn(null);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(owner, bookingId));
    }

    @Test
    public void testGetAllBookingByBookerId() {
        Long bookerId = 1L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());

        List<BookingOutputDto> bookingsOut = new ArrayList<>();
        bookingsOut.add(new BookingOutputDto());

        PageImpl<Booking> page = new PageImpl<>(bookings);

        User user = new User(bookerId, "name", "name@email.com");

        when(userService.validUser(bookerId)).thenReturn(user);
        when(bookingRepository.findAllByBookerId(eq(bookerId), any(Pageable.class))).thenReturn(page);
        when(bookingMapper.transformBookingToBookingOutputDto(any())).thenReturn(new BookingOutputDto());

        List<BookingOutputDto> result = bookingService.getAllBookingByBookerId(bookerId, state, from, size);

        verify(userService).validUser(bookerId);
        verify(bookingRepository).findAllByBookerId(eq(bookerId), any(Pageable.class));
        verify(bookingMapper, times(bookings.size())).transformBookingToBookingOutputDto(any());
        assertNotNull(result);
        assertEquals(bookings.size(), result.size());
    }

    @Test
    public void testGetAllBookingByOwnerId() {
        Long owner = 1L;
        Long booker = 2L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;

        User user = new User();
        user.setId(owner);

        User userBooker = new User();
        user.setId(booker);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        List<Item> items = new ArrayList<>();
        items.add(item);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(userBooker);

        List<Booking> bookings = List.of(booking);

        when(userService.validUser(owner)).thenReturn(user);
        when(itemRepository.findAllByOwnerId(owner, PageRequest.of(0, Integer.MAX_VALUE))).thenReturn(new PageImpl<>(items));
        when(bookingRepository.findAll(PageRequest.of(0, Integer.MAX_VALUE))).thenReturn(new PageImpl<>(bookings));

        List<BookingOutputDto> result = bookingService.getAllBookingByOwnerId(owner, state, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findAll(PageRequest.of(0, Integer.MAX_VALUE));
        verify(userService, times(1)).validUser(owner);
        verify(itemRepository, times(1)).findAllByOwnerId(owner, PageRequest.of(0, Integer.MAX_VALUE));
        verify(bookingMapper, times(1)).transformBookingToBookingOutputDto(booking);
    }

    @Test
    public void testGetBookingByStateAll() {
        User user1 = new User(1L, "name1", "email1@email.com");
        User user2 = new User(2L, "name2", "email2@email.com");
        User user3 = new User(3L, "name3", "email3@email.com");

        Item item1 = new Item();
        item1.setId(1L);
        Item item2 = new Item();
        item1.setId(2L);
        Item item3 = new Item();
        item1.setId(3L);

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setItem(item1);
        booking1.setBooker(user1);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setItem(item2);
        booking2.setBooker(user2);

        Booking booking3 = new Booking();
        booking3.setId(2L);
        booking3.setStatus(BookingStatus.WAITING);
        booking3.setItem(item3);
        booking3.setBooker(user3);
        List<Booking> savedBooking = Arrays.asList(
                booking1,
                booking2,
                booking3
        );
        List<BookingOutputDto> expectedBooking = savedBooking.stream()
                .map(bookingMapper::transformBookingToBookingOutputDto)
                .collect(Collectors.toList());
        BookingServiceImp bookingService = new BookingServiceImp(bookingRepository, userService, itemRepository, bookingMapper);

        List<BookingOutputDto> actualBooking = bookingService.getBookingByState(savedBooking, "ALL");

        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    public void testGetBookingByStateWaiting() {
        Long owner1 = 1L;
        User user1 = new User();
        user1.setId(owner1);

        Long booker1 = 2L;
        User userBooker1 = new User();
        userBooker1.setId(booker1);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setOwner(user1);

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setItem(item1);
        booking1.setBooker(userBooker1);

        Long owner2 = 2L;
        User user2 = new User();
        user2.setId(owner2);

        Long booker2 = 3L;
        User userBooker2 = new User();
        userBooker2.setId(booker2);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setOwner(user1);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setItem(item2);
        booking2.setBooker(userBooker2);


        List<Booking> savedBooking = Arrays.asList(
                booking1,
                booking2
        );

        List<BookingOutputDto> expectedBooking = savedBooking.stream()
                .filter(x -> x.getStatus().equals(BookingStatus.WAITING))
                .map(bookingMapper::transformBookingToBookingOutputDto)
                .collect(Collectors.toList());

        List<BookingOutputDto> actualBooking = bookingService.getBookingByState(savedBooking, "WAITING");

        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    public void testGetBookingByWrongState() {
        List<Booking> savedBooking = Arrays.asList(
                new Booking(),
                new Booking(),
                new Booking()
        );

        assertThrows(BookingStateException.class, () -> bookingService.getBookingByState(savedBooking, "CRINGE"));
    }

    @Test
    public void testGetLastBooking_NoBookingFound() {

        Item item = new Item();
        item.setId(1L);
        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(anyLong(), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(null);

        BookingItemDto result = bookingService.getLastBooking(item, BookingStatus.APPROVED);

        assertNull(result);
    }

    @Test
    public void testGetLastBooking_BookingFoundWithApprovedStatus() {
        Item item = new Item();
        item.setId(1L);
        User user = new User();
        user.setId(1L);

        Booking savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setBooker(user);
        savedBooking.setItem(item);
        savedBooking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(anyLong(), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(savedBooking);
        BookingItemDto expected = new BookingItemDto(savedBooking.getId(), savedBooking.getBooker().getId());

        BookingItemDto result = bookingService.getLastBooking(item, BookingStatus.APPROVED);

        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getBookerId(), result.getBookerId());
    }

    @Test
    public void testGetNextBookingReturnsBookingItemDtoWhenUpcomingBookingFound() {
        Item item = new Item();
        item.setId(1L);
        BookingStatus status = BookingStatus.APPROVED;
        User user = new User();
        user.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                anyLong(), any(LocalDateTime.class), eq(status)))
                .thenReturn(booking);

        BookingItemDto expectedBookingItemDto = new BookingItemDto(1L, 2L);
        when(bookingMapper.transformBookingToBookingItemDto(booking))
                .thenReturn(expectedBookingItemDto);

        BookingItemDto result = bookingService.getNextBooking(item, status);

        assertNotNull(result);
        assertEquals(expectedBookingItemDto, result);
    }
}