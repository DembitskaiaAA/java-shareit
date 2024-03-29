package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(Long booker, BookingInputDto bookingInputDto) {
        return post("", booker, null, bookingInputDto);
    }

    public ResponseEntity<Object> approveBooking(Long owner, long bookingId, Boolean approved) {
        return patch("/" + bookingId + "?approved={approved}", owner, Map.of("approved", approved), null);
    }

    public ResponseEntity<Object> getBooking(Long owner, long bookingId) {
        return get("/" + bookingId, owner, null);
    }

    public ResponseEntity<Object> getAllBookingByBookerId(Long booker, String state, Integer from, Integer size) {
        Map<String, Object> params = Map.of("state", state, "from", from, "size", size);
        return get("?state={state}&from={from}&size={size}", booker, params);
    }

    public ResponseEntity<Object> getAllBookingByOwnerId(Long owner, String state, Integer from, Integer size) {
        Map<String, Object> params = Map.of("state", state, "from", from, "size", size);
        return get("/owner?state={state}&from={from}&size={size}", owner, params);
    }
}