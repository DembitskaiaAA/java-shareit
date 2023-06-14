package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long ownerId);

    Booking findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime end, BookingStatus status);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime end, BookingStatus status);

    Booking findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(Long itemId, Long userId,
                                                                LocalDateTime end, BookingStatus status);
}
