package com.njenga.teacher_reading_portal.assignment;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateAssignmentRequest(
        @NotNull Long bookId,
        @NotNull Long studentId,
        @NotNull @FutureOrPresent LocalDate dueDate
) {
}
