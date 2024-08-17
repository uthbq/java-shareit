package ru.practicum.shareit.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, StartEnd> {

    @Override
    public void initialize(final StartBeforeEnd constraintAnnotation) {
    }

    @Override
    public boolean isValid(StartEnd value, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = value.getStart();
        LocalDateTime end = value.getEnd();
        return start.isBefore(end);
    }
}



