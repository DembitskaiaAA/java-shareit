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
}