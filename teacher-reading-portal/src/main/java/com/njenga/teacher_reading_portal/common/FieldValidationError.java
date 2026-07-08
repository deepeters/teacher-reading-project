package com.njenga.teacher_reading_portal.common;

public record FieldValidationError(
        String field,
        String message
) {
}
