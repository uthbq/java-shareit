package ru.practicum.shareit.exception;

public class NotAvailableItem extends RuntimeException {

    public NotAvailableItem(String message) {
        super(message);
    }
}