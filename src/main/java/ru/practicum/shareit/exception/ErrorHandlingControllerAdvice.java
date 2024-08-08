package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            NotAvailableItem.class,
            WrongDateException.class,
            AccessDeniedException.class,
            BookingStatusException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestExceptions(Exception e) {
        if (e instanceof ConstraintViolationException) {
            ConstraintViolationException ex = (ConstraintViolationException) e;
            final List<Violation> violations = ex.getConstraintViolations().stream()
                    .map(violation -> new Violation(violation.getPropertyPath().toString(), violation.getMessage()))
                    .toList();
            return new ErrorResponse(violations.toString());
        } else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            final List<Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                    .toList();
            return new ErrorResponse(violations.toString());
        } else {
            return new ErrorResponse(e.getMessage());
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.info("NotFound: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class,
            DuplicateDataException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictExceptions(Exception e) {
        if (e instanceof DataIntegrityViolationException) {
            log.error("Unique constraint violation: {}", e.getMessage());
            return new ErrorResponse("Email уже существует");
        } else {
            return new ErrorResponse(e.getMessage());
        }
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }
}