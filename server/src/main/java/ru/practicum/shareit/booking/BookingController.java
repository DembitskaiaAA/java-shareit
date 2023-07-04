package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Component
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingOutputDto createBooking(@RequestHeader("X-Sharer-User-Id") Long booker,
                                          @RequestBody BookingInputDto bookingInputDto) {
        return bookingService.createBooking(booker, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long owner,
                                           @PathVariable long bookingId,
                                           @RequestParam Boolean approved) {
        return bookingService.approveBooking(owner, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBooking(@RequestHeader("X-Sharer-User-Id") Long owner,
                                       @PathVariable long bookingId) {
        return bookingService.getBooking(owner, bookingId);
    }

    @GetMapping()
    public List<BookingOutputDto> getAllBookingByBookerId(@RequestHeader("X-Sharer-User-Id") Long booker,
                                                          @RequestParam String state,
                                                          @RequestParam Integer from,
                                                          @RequestParam Integer size) {
        return bookingService.getAllBookingByBookerId(booker, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> getAllBookingByOwnerId(@RequestHeader("X-Sharer-User-Id") Long owner,
                                                         @RequestParam String state,
                                                         @RequestParam Integer from,
                                                         @RequestParam Integer size) {
        return bookingService.getAllBookingByOwnerId(owner, state, from, size);
    }















/*    @PostMapping
    public BookingOutputDto createBooking(@RequestHeader("X-Sharer-User-Id") Long booker,
                                          @Valid @RequestBody BookingInputDto bookingInputDto) {
        return bookingService.createBooking(booker, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long owner,
                                           @PathVariable long bookingId,
                                           @RequestParam Boolean approved) {
        return bookingService.approveBooking(owner, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBooking(@RequestHeader("X-Sharer-User-Id") Long owner, @PathVariable long bookingId) {
        return bookingService.getBooking(owner, bookingId);
    }

    @GetMapping()
    public List<BookingOutputDto> getAllBookingByBookerId(@RequestHeader("X-Sharer-User-Id") Long booker,
                                                          @RequestParam(required = false, defaultValue = "ALL") String state,
                                                          @RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                                          @RequestParam(defaultValue = "20", required = false) @Min(value = 1) Integer size) {
        return bookingService.getAllBookingByBookerId(booker, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> getAllBookingByOwnerId(@RequestHeader("X-Sharer-User-Id") Long owner,
                                                         @RequestParam(required = false, defaultValue = "ALL") String state,
                                                         @RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                                         @RequestParam(defaultValue = "20", required = false) @Min(value = 1) Integer size) {
        return bookingService.getAllBookingByOwnerId(owner, state, from, size);
    }*/

}
