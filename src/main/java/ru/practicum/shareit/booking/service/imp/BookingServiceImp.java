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
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImp implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;


    public BookingServiceImp(BookingRepository bookingRepository,
                             UserRepository userRepository,
                             UserService userService, @Lazy ItemService itemService, ItemRepository itemRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.itemService = itemService;
        this.itemRepository = itemRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public BookingOutputDto createBooking(Long booker, BookingInputDto bookingInputDto) {
        if (bookingInputDto.getStart().isAfter(bookingInputDto.getEnd())
                || bookingInputDto.getStart().isEqual(bookingInputDto.getEnd())) {
            throw new BookingTimeException("При бронировании ошибка: " +
                    "неверно указано время начала или окончания бронирования");
        }

        User savedUser = userService.validUser(booker);
        Item savedItem = itemService.validItem(bookingInputDto.getItemId());
        if (savedItem.getOwner().getId() == booker) {
            throw new NotFoundException(String.format("При бронировании ошибка: " +
                            "владелец с id: %s не может забронировать свой товар с id: %s",
                    booker, savedItem.getId()));
        }
        if (!savedItem.getAvailable()) {
            throw new NotAvailableException(String.format("При бронировании ошибка: " +
                    "товар с id: %s уже заброванирован", savedItem.getId()));
        }
        Booking booking = bookingMapper.transformBookingInputDtoToBooking(bookingInputDto);
        booking.setBooker(savedUser);
        booking.setItem(savedItem);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking;
        savedBooking = bookingRepository.save(booking);
        return bookingMapper.transformBookingToBookingOutputDto(savedBooking);
    }

    @Override
    public BookingOutputDto approveBooking(Long owner, Long bookingId, Boolean approved) {
        userService.validUser(owner);
        Booking savedBooking = validBooking(bookingId);
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
        return bookingMapper.transformBookingToBookingOutputDto(resultBooking);
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
        return bookingMapper.transformBookingToBookingOutputDto(savedBooking);
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
                booking = savedBooking.stream().map(bookingMapper::transformBookingToBookingOutputDto).collect(Collectors.toList());
                break;
            }
            case "WAITING": {
                booking = savedBooking.stream().filter(x -> x.getStatus().equals(BookingStatus.WAITING))
                        .map(bookingMapper::transformBookingToBookingOutputDto).collect(Collectors.toList());
                break;
            }
            case "REJECTED": {
                booking = savedBooking.stream().filter(x -> x.getStatus().equals(BookingStatus.REJECTED))
                        .map(bookingMapper::transformBookingToBookingOutputDto).collect(Collectors.toList());
                break;
            }
            case "CURRENT": {
                booking = savedBooking.stream().filter(x -> (now.isAfter(x.getStart()) && now.isBefore(x.getEnd())))
                        .map(bookingMapper::transformBookingToBookingOutputDto).collect(Collectors.toList());
                break;
            }
            case "PAST": {
                booking = savedBooking.stream().filter(x -> (now.isAfter(x.getEnd())))
                        .map(bookingMapper::transformBookingToBookingOutputDto).collect(Collectors.toList());
                break;
            }
            case "FUTURE": {
                booking = savedBooking.stream().filter(x -> (now.isBefore(x.getStart())))
                        .map(bookingMapper::transformBookingToBookingOutputDto).collect(Collectors.toList());
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
        if (savedBooking == null) {
            return null;
        }
        return bookingMapper.transformBookingToBookingItemDto(savedBooking);
    }

    @Override
    public BookingItemDto getNextBooking(Item item, BookingStatus state) {
        Booking savedBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(),
                LocalDateTime.now(), BookingStatus.APPROVED);
        return bookingMapper.transformBookingToBookingItemDto(savedBooking);
    }

    @Override
    public Booking getBookingByItemIdBookerIdForComment(Long itemId, Long userId) {
        return bookingRepository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(itemId, userId,
                LocalDateTime.now(), BookingStatus.APPROVED);
    }

    @Override
    public Booking validBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("При подтверждении бронирования ошибка: " +
                        "бронирование с id: %s отсутствует", bookingId)));
    }
}
