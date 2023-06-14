package ru.practicum.shareit.exceptions;

public class BookingStateException extends RuntimeException {
    public BookingStateException(String msg) {
        super(msg);
    }
}