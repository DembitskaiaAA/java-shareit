package ru.practicum.shareit.exceptions;

public class RepeatedValueException extends RuntimeException {
    public RepeatedValueException(String msg) {
        super(msg);
    }
}