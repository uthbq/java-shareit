package ru.practicum.shareit.exception;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@SpringBootTest
class ErrorHandlingControllerAdviceTest {

    private final ErrorHandlingControllerAdvice errorHandler;


    @Test
    void handleIllegalArgumentException() {
        var exception = new IllegalArgumentException("Test exception");

        var result = errorHandler.handleIllegalArgumentException(exception);

        assertNotNull(result);
        assertEquals(exception.getMessage(), result.getError());
    }

    @Test
    void handleNotAvailableItem() {
        var exception = new NotAvailableItem("Test exception");

        var result = errorHandler.handleNotAvailableItem(exception);

        assertNotNull(result);
        assertEquals(exception.getMessage(), result.getError());
    }

    @Test
    void handleWrongDateException() {
        var exception = new WrongDateException("Test exception");
        var result = errorHandler.handleWrongDateException(exception);
        assertNotNull(result);
        assertEquals(exception.getMessage(), result.getError());

    }

    @Test
    void handleNotFoundException() {
        var exception = new NotFoundException("Test exception");
        var result = errorHandler.handleNotFoundException(exception);
        assertNotNull(result);
        assertEquals(exception.getMessage(), result.getError());
    }

    @Test
    void handleJdbcSQLIntegrityConstraintViolationException() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Test exception");
        var result = errorHandler.handleJdbcSQLIntegrityConstraintViolationException(exception);
        assertNotNull(result);
        assertEquals(exception.getMessage(), result.getError());
    }

    @Test
    void handleAccessDeniedException() {
        var exception = new AccessDeniedException("Test exception");
        var result = errorHandler.handleAccessDeniedException(exception);
        assertNotNull(result);
        assertEquals(exception.getMessage(), result.getError());
    }

    @Test
    void handleDuplicateDataException() {
        var exception = new DuplicateDataException("Test exception");
        var result = errorHandler.handleDuplicateDataException(exception);
        assertNotNull(result);
        assertEquals(exception.getMessage(), result.getError());
    }

    @Test
    void handleBookingStatusException() {
        var exception = new BookingStatusException("Test exception");
        var result = errorHandler.handleBookingStatusException(exception);
        assertNotNull(result);
        assertEquals(exception.getMessage(), result.getError());
    }

    @Test
    void handleRuntimeException() {
        var exception = new RuntimeException("Test exception");
        var result = errorHandler.handleRuntimeException(exception);
        assertNotNull(result);
        assertEquals(exception.getMessage(), result.getError());
    }
}