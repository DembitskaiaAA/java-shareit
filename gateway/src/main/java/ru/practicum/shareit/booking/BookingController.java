package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long booker,
                                                @Valid @RequestBody BookingInputDto bookingInputDto) {
        return bookingClient.createBooking(booker, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long owner,
                                                 @PathVariable long bookingId,
                                                 @RequestParam Boolean approved) {
        return bookingClient.approveBooking(owner, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long owner,
                                             @PathVariable long bookingId) {
        return bookingClient.getBooking(owner, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllBookingByBookerId(@RequestHeader("X-Sharer-User-Id") Long booker,
                                                          @RequestParam(required = false, defaultValue = "ALL") String state,
                                                          @RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                                          @RequestParam(defaultValue = "20", required = false) @Min(value = 1) Integer size) {
        return bookingClient.getAllBookingByBookerId(booker, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingByOwnerId(@RequestHeader("X-Sharer-User-Id") Long owner,
                                                         @RequestParam(required = false, defaultValue = "ALL") String state,
                                                         @RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                                         @RequestParam(defaultValue = "20", required = false) @Min(value = 1) Integer size) {
        return bookingClient.getAllBookingByOwnerId(owner, state, from, size);
    }














/*    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }*/
}