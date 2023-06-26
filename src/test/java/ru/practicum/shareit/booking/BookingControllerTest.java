package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.BookingStateException;
import ru.practicum.shareit.exceptions.BookingTimeException;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBookerDto;
import ru.practicum.shareit.user.dto.UserBookerDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    public void createItem_ReturnsBookingOutputDto() throws Exception {
        Long booker = 1L;
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        BookingInputDto requestDto = new BookingInputDto(
                LocalDateTime.of(2023, 7, 1, 12, 0),
                LocalDateTime.of(2023, 7, 3, 12, 0),
                1L
        );
        BookingOutputDto expectedResponse = new BookingOutputDto(
                1L,
                requestDto.getStart(),
                requestDto.getEnd(),
                BookingStatus.WAITING,
                new UserBookerDto(booker),
                new ItemBookerDto(requestDto.getItemId(), "test item")
        );
        given(bookingService.createBooking(booker, requestDto))
                .willReturn(expectedResponse);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booker)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                .andExpect(jsonPath("$.start").value(expectedResponse.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(expectedResponse.getEnd().format(formatter)))
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus().name()))
                .andExpect(jsonPath("$.booker.id").value(expectedResponse.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(expectedResponse.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(expectedResponse.getItem().getName()));

        verify(bookingService, times(1)).createBooking(booker, requestDto);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void approveBooking_ReturnsBookingOutputDto() throws Exception {
        Long owner = 1L;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        Long bookingId = 2L;
        Boolean approved = true;
        BookingOutputDto expectedResponse = new BookingOutputDto(
                bookingId,
                LocalDateTime.of(2023, 7, 1, 12, 0),
                LocalDateTime.of(2023, 7, 3, 12, 0),
                BookingStatus.WAITING,
                new UserBookerDto(owner),
                new ItemBookerDto(1L, "test item")
        );
        given(bookingService.approveBooking(owner, bookingId, approved))
                .willReturn(expectedResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", owner)
                        .param("approved", approved.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                .andExpect(jsonPath("$.start").value(expectedResponse.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(expectedResponse.getEnd().format(formatter)))
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus().name()))
                .andExpect(jsonPath("$.booker.id").value(expectedResponse.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(expectedResponse.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(expectedResponse.getItem().getName()));

        verify(bookingService, times(1)).approveBooking(owner, bookingId, approved);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void getBooking_ReturnsBookingOutputDto() throws Exception {
        Long owner = 1L;
        Long bookingId = 2L;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        BookingOutputDto expectedResponse = new BookingOutputDto(
                bookingId,
                LocalDateTime.of(2023, 7, 1, 12, 0),
                LocalDateTime.of(2023, 7, 3, 12, 0),
                BookingStatus.WAITING,
                new UserBookerDto(owner),
                new ItemBookerDto(1L, "test item")
        );
        given(bookingService.getBooking(owner, bookingId))
                .willReturn(expectedResponse);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", owner))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                .andExpect(jsonPath("$.start").value(expectedResponse.getStart().format(formatter)))
                .andExpect(jsonPath("$.end").value(expectedResponse.getEnd().format(formatter)))
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus().name()))
                .andExpect(jsonPath("$.booker.id").value(expectedResponse.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(expectedResponse.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(expectedResponse.getItem().getName()));

        verify(bookingService, times(1)).getBooking(owner, bookingId);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void getAllBookingByBookerId_ReturnsListOfBookingOutputDto() throws Exception {
        Long booker = 1L;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String state = "ALL";
        Integer from = 0;
        Integer size = 20;
        List<BookingOutputDto> expectedResponse = Arrays.asList(
                new BookingOutputDto(
                        1L,
                        LocalDateTime.of(2023, 7, 1, 12, 0),
                        LocalDateTime.of(2023, 7, 3, 12, 0),
                        BookingStatus.WAITING,
                        new UserBookerDto(booker),
                        new ItemBookerDto(1L, "test item")
                ),
                new BookingOutputDto(
                        2L,
                        LocalDateTime.of(2023, 8, 1, 12, 0),
                        LocalDateTime.of(2023, 8, 3, 12, 0),
                        BookingStatus.CANCELED,
                        new UserBookerDto(booker),
                        new ItemBookerDto(2L, "test item 2")
                )
        );
        given(bookingService.getAllBookingByBookerId(booker, state, from, size))
                .willReturn(expectedResponse);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker)
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedResponse.get(0).getId()))
                .andExpect(jsonPath("$[0].start").value(expectedResponse.get(0).getStart().format(formatter)))
                .andExpect(jsonPath("$[0].end").value(expectedResponse.get(0).getEnd().format(formatter)))
                .andExpect(jsonPath("$[0].status").value(expectedResponse.get(0).getStatus().name()))
                .andExpect(jsonPath("$[0].booker.id").value(expectedResponse.get(0).getBooker().getId()))
                .andExpect(jsonPath("$[0].item.id").value(expectedResponse.get(0).getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(expectedResponse.get(0).getItem().getName()))
                .andExpect(jsonPath("$[1].id").value(expectedResponse.get(1).getId()))
                .andExpect(jsonPath("$[1].start").value(expectedResponse.get(1).getStart().format(formatter)))
                .andExpect(jsonPath("$[1].end").value(expectedResponse.get(1).getEnd().format(formatter)))
                .andExpect(jsonPath("$[1].status").value(expectedResponse.get(1).getStatus().name()))
                .andExpect(jsonPath("$[1].booker.id").value(expectedResponse.get(1).getBooker().getId()))
                .andExpect(jsonPath("$[1].item.id").value(expectedResponse.get(1).getItem().getId()))
                .andExpect(jsonPath("$[1].item.name").value(expectedResponse.get(1).getItem().getName()));

        verify(bookingService, times(1)).getAllBookingByBookerId(booker, state, from, size);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void getAllBookingByOwnerId_ReturnsListOfBookingOutputDto() throws Exception {
        Long owner = 1L;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String state = "ALL";
        Integer from = 0;
        Integer size = 20;
        List<BookingOutputDto> expectedResponse = Arrays.asList(
                new BookingOutputDto(
                        1L,
                        LocalDateTime.of(2023, 7, 1, 12, 0),
                        LocalDateTime.of(2023, 7, 3, 12, 0),
                        BookingStatus.WAITING,
                        new UserBookerDto(2L),
                        new ItemBookerDto(1L, "test item")
                ),
                new BookingOutputDto(
                        2L,
                        LocalDateTime.of(2023, 8, 1, 12, 0),
                        LocalDateTime.of(2023, 8, 3, 12, 0),
                        BookingStatus.CANCELED,
                        new UserBookerDto(3L),
                        new ItemBookerDto(2L, "test item 2")
                )
        );
        given(bookingService.getAllBookingByOwnerId(owner, state, from, size))
                .willReturn(expectedResponse);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner)
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedResponse.get(0).getId()))
                .andExpect(jsonPath("$[0].start").value(expectedResponse.get(0).getStart().format(formatter)))
                .andExpect(jsonPath("$[0].end").value(expectedResponse.get(0).getEnd().format(formatter)))
                .andExpect(jsonPath("$[0].status").value(expectedResponse.get(0).getStatus().name()))
                .andExpect(jsonPath("$[0].booker.id").value(expectedResponse.get(0).getBooker().getId()))
                .andExpect(jsonPath("$[0].item.id").value(expectedResponse.get(0).getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(expectedResponse.get(0).getItem().getName()))
                .andExpect(jsonPath("$[1].id").value(expectedResponse.get(1).getId()))
                .andExpect(jsonPath("$[1].start").value(expectedResponse.get(1).getStart().format(formatter)))
                .andExpect(jsonPath("$[1].end").value(expectedResponse.get(1).getEnd().format(formatter)))
                .andExpect(jsonPath("$[1].status").value(expectedResponse.get(1).getStatus().name()))
                .andExpect(jsonPath("$[1].booker.id").value(expectedResponse.get(1).getBooker().getId()))
                .andExpect(jsonPath("$[1].item.id").value(expectedResponse.get(1).getItem().getId()))
                .andExpect(jsonPath("$[1].item.name").value(expectedResponse.get(1).getItem().getName()));

        verify(bookingService, times(1)).getAllBookingByOwnerId(owner, state, from, size);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void testNotFoundStatus() throws Exception {
        Long bookingId = 1L;
        Long userId = 1L;
        when(bookingService.getBooking(userId, bookingId)).thenThrow(new NotFoundException("Not valid ids"));
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not valid ids"));
    }

    @Test
    public void testBadRequestStatusThrowNotAvailableException() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        BookingInputDto bookingInputDto = new BookingInputDto(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), itemId);
        given(bookingService.createBooking(userId, bookingInputDto)).willThrow(new NotAvailableException("Not valid booking"));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingInputDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Not valid booking"));
    }

    @Test
    public void testBadRequestStatusThrowBookingTimeException() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        BookingInputDto bookingInputDto = new BookingInputDto(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), itemId);
        given(bookingService.createBooking(userId, bookingInputDto)).willThrow(new BookingTimeException("Not valid booking time"));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingInputDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Not valid booking time"));
    }

    @Test
    public void testBadRequestStatusThrowBookingStateException() throws Exception {
        Long owner = 1L;
        Long itemId = 1L;
        Integer from = 0;
        Integer size = 10;
        String status = "APPROVED";
        given(bookingService.getAllBookingByOwnerId(owner, status, from, size)).willThrow(new BookingStateException("Not valid state"));
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner)
                        .param("state", status)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Not valid state"));
    }

}