package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testFindAllByBookerId() {
        User booker = new User(null, "bookerName", "bookerEmail@example.com");
        entityManager.persist(booker);

        User owner = new User(null, "ownerName", "ownerEmail@example.com");
        entityManager.persist(owner);

        Item item1 = new Item(null, "item1", "description1", true, owner, null);
        entityManager.persist(item1);

        LocalDateTime start1 = LocalDateTime.of(2023, 6, 26, 14, 0);
        LocalDateTime end1 = LocalDateTime.of(2023, 6, 26, 16, 0);
        Booking booking1 = new Booking();
        booking1.setStart(start1);
        booking1.setEnd(end1);
        booking1.setItem(item1);
        booking1.setBooker(booker);
        booking1.setStatus(BookingStatus.APPROVED);
        entityManager.persist(booking1);

        LocalDateTime start2 = LocalDateTime.of(2023, 6, 27, 10, 0);
        LocalDateTime end2 = LocalDateTime.of(2023, 6, 27, 12, 0);
        Booking booking2 = new Booking();
        booking2.setStart(start2);
        booking2.setEnd(end2);
        booking2.setItem(item1);
        booking2.setBooker(booker);
        booking2.setStatus(BookingStatus.APPROVED);
        entityManager.persist(booking2);

        Page<Booking> result = bookingRepository.findAllByBookerId(booker.getId(), PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().contains(booking1));
        assertTrue(result.getContent().contains(booking2));
    }

    @Test
    void testFindFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc() {
        User user = new User(null, "John Doe", "john.doe@example.com");
        entityManager.persist(user);
        Item item = new Item(null, "Item 1", "Описание для товара 1", true, user, null);
        entityManager.persist(item);
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Booking booking1 = new Booking(null, start.minusHours(2), end.minusHours(1), item, user, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, start.minusHours(1), end.plusHours(1), item, user, BookingStatus.WAITING);
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        bookingRepository.saveAll(bookings);

        Booking result = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                item.getId(), end, BookingStatus.APPROVED);

        assertEquals(booking1, result);
    }

    @Test
    void testFindFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus() {
        User user = new User(null, "John Doe", "john.doe@example.com");
        entityManager.persist(user);
        Item item = new Item(null, "Item 1", "Описание для товара 1", true, user, null);
        entityManager.persist(item);
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Booking booking1 = new Booking(null, start.minusHours(2), end.minusHours(1), item, user, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null, start.minusHours(1), end.plusHours(1), item, user, BookingStatus.REJECTED);
        Booking booking3 = new Booking(null, start.plusHours(1), end.plusHours(2), item, user, BookingStatus.WAITING);
        List<Booking> bookings = Arrays.asList(booking1, booking2, booking3);

        bookingRepository.saveAll(bookings);

        Booking result = bookingRepository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(
                item.getId(), user.getId(), end, BookingStatus.APPROVED);

        assertEquals(booking1, result);
    }
}