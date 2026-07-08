package com.njenga.teacher_reading_portal.assignment;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AssignmentResponse(
        Long id,
        Long bookId,
        String bookTitle,
        String bookAuthor,
        Long teacherId,
        String teacherName,
        Long studentId,
        String studentName,
        LocalDate dueDate,
        AssignmentStatus status,
        Integer minutesRead,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AssignmentResponse from(Assignment assignment) {
        return new AssignmentResponse(
                assignment.getId(),
                assignment.getBook().getId(),
                assignment.getBook().getTitle(),
                assignment.getBook().getAuthor(),
                assignment.getTeacher().getId(),
                assignment.getTeacher().getName(),
                assignment.getStudent().getId(),
                assignment.getStudent().getName(),
                assignment.getDueDate(),
                assignment.getStatus(),
                assignment.getMinutesRead(),
                assignment.getCreatedAt(),
                assignment.getUpdatedAt()
        );
    }
}
