package ru.practicum.shareit.booking.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.BookingStateException;
import ru.practicum.shareit.exceptions.BookingTimeException;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImp implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    public BookingServiceImp(@Lazy BookingRepository bookingRepository, UserRepository userRepository,
                             ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingOutputDto createBooking(Long booker, BookingInputDto bookingInputDto) {
        if (bookingInputDto.getStart().isBefore(LocalDateTime.now())
                || bookingInputDto.getStart().isAfter(bookingInputDto.getEnd())
                || bookingInputDto.getStart().isEqual(bookingInputDto.getEnd())
                || bookingInputDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BookingTimeException("При бронировании ошибка: " +
                    "неверно указано время начала или окончания бронирования");
        }

        User savedUser = userRepository.findById(booker).orElseThrow(() -> new NotFoundException(
                String.format("При бронировании ошибка: пользователь с id: %s отсутствует", booker)));
        Item savedItem = itemRepository.findById(bookingInputDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("При бронировании ошибка: " +
                        "товар с id: %s отсутствует", bookingInputDto.getItemId())));
        if (savedItem.getOwner().getId() == booker) {
            throw new NotFoundException(String.format("При бронировании ошибка: " +
                            "владелец с id: %s не может забронировать свой товар с id: %s",
                    savedItem.getOwner().getId(), bookingInputDto.getItemId()));
        }
        if (!savedItem.getAvailable()) {
            throw new NotAvailableException(String.format("При бронировании ошибка: " +
                    "товар с id: %s уже заброванирован", bookingInputDto.getItemId()));
        }
        Booking booking = BookingMapper.toBooking(bookingInputDto);
        booking.setBooker(savedUser);
        booking.setItem(savedItem);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking;
        try {
            savedBooking = bookingRepository.save(booking);
        } catch (Exception e) {
            log.error("Ошибка при сохранении бронирования: {}", e.getMessage());
            throw e;
        }
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingOutputDto approveBooking(Long owner, Long bookingId, Boolean approved) {
        userRepository.findById(owner).orElseThrow(() -> new NotFoundException(
                String.format("При подтверждении бронирования ошибка: пользователь с id: %s отсутствует", owner)));
        Booking savedBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("При подтверждении бронирования ошибка: " +
                        "бронирование с id: %s отсутствует", bookingId)));
        if (savedBooking.getItem().getOwner().getId() != owner) {
            throw new NotFoundException(String.format("При подтверждении бронирования ошибка: " +
                    "пользователь с id: %s не является владельцем товара", owner));
        }
        if (savedBooking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new NotAvailableException(String.format("При подтверждении бронирования ошибка: " +
                    "бронирование товара с id: %s уже подтверждено", owner));
        }
        if (approved) {
            savedBooking.setStatus(BookingStatus.APPROVED);
        } else {
            savedBooking.setStatus(BookingStatus.REJECTED);
        }
        Booking resultBooking = bookingRepository.save(savedBooking);
        return BookingMapper.toBookingDto(resultBooking);
    }

    @Override
    public BookingOutputDto getBooking(Long owner, Long bookingId) {
        userRepository.findById(owner).orElseThrow(() -> new NotFoundException(
                String.format("При получении информации о бронировании ошибка: " +
                        "пользователь с id: %s отсутствует", owner)));
        Booking savedBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("При получении информации о бронировании ошибка: " +
                        "бронирование с id: %s отсутствует", bookingId)));
        if (savedBooking.getItem().getOwner().getId() != owner && savedBooking.getBooker().getId() != owner) {
            throw new NotFoundException(String.format("При получении информации о бронировании ошибка: " +
                    "пользователь с id: %s не является владельцем товара или осуществляющим бронирование", owner));
        }
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public List<BookingOutputDto> getAllBookingByBookerId(Long booker, String state) {
        userRepository.findById(booker)
                .orElseThrow(() -> new NotFoundException(
                        String.format("При получении информации о бронированях пользователя ошибка: " +
                                "пользователь с id: %s отсутствует", booker)));
        List<Booking> savedBooking = bookingRepository.findAllByBookerId(booker);
        return getBookingByState(savedBooking, state)
                .stream()
                .sorted(Comparator.comparing(BookingOutputDto::getStart, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingOutputDto> getAllBookingByOwnerId(Long owner, String state) {
        userRepository.findById(owner)
                .orElseThrow(() -> new NotFoundException(
                        String.format("При получении информации о бронированях владельца товаров ошибка: " +
                                "пользователь с id: %s отсутствует", owner)));
        List<Item> items = itemRepository.findAllByOwnerId(owner);
        List<Booking> savedBooking = bookingRepository.findAll().stream().filter(x -> items.contains(x.getItem()))
                .collect(Collectors.toList());
        return getBookingByState(savedBooking, state)
                .stream()
                .sorted(Comparator.comparing(BookingOutputDto::getStart, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingOutputDto> getBookingByState(List<Booking> savedBooking, String state) {
        List<BookingOutputDto> booking;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL": {
                booking = savedBooking.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            }
            case "WAITING": {
                booking = savedBooking.stream().filter(x -> x.getStatus().equals(BookingStatus.WAITING))
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            }
            case "REJECTED": {
                booking = savedBooking.stream().filter(x -> x.getStatus().equals(BookingStatus.REJECTED))
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            }
            case "CURRENT": {
                booking = savedBooking.stream().filter(x -> (now.isAfter(x.getStart()) && now.isBefore(x.getEnd())))
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            }
            case "PAST": {
                booking = savedBooking.stream().filter(x -> (now.isAfter(x.getEnd())))
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            }
            case "FUTURE": {
                booking = savedBooking.stream().filter(x -> (now.isBefore(x.getStart())))
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                break;
            }
            default: {
                throw new BookingStateException(String.format("Unknown state: UNSUPPORTED_STATUS"));
            }

        }
        return booking;
    }

    @Override
    public BookingItemDto getLastBooking(Item item, BookingStatus state) {
        Booking savedBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(item.getId(),
                LocalDateTime.now(), BookingStatus.APPROVED);
        return BookingMapper.toBookingItemDto(savedBooking);
    }

    @Override
    public BookingItemDto getNextBooking(Item item, BookingStatus state) {
        Booking savedBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(),
                LocalDateTime.now(), BookingStatus.APPROVED);
        return BookingMapper.toBookingItemDto(savedBooking);
    }

    @Override
    public Booking getBookingByUserIdItemIdForComment(Long itemId, Long userId) {
        return bookingRepository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(itemId, userId,
                LocalDateTime.now(), BookingStatus.APPROVED);
    }
}
