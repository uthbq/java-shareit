package ru.practicum.shareit.validator;

import jakarta.validation.Constraint;

@Constraint(validatedBy = StartBeforeEndValidator.class)
public @interface StartBeforeEnd {
    String message() default "End time must be after start time";
}
