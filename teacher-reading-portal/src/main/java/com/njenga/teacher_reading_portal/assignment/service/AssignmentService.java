package com.njenga.teacher_reading_portal.assignment.service;

import com.njenga.teacher_reading_portal.assignment.Assignment;
import com.njenga.teacher_reading_portal.assignment.AssignmentRepository;
import com.njenga.teacher_reading_portal.assignment.AssignmentStatus;
import com.njenga.teacher_reading_portal.assignment.dto.AssignmentResponse;
import com.njenga.teacher_reading_portal.assignment.dto.CreateAssignmentRequest;
import com.njenga.teacher_reading_portal.assignment.dto.UpdateAssignmentProgressRequest;
import com.njenga.teacher_reading_portal.book.Book;
import com.njenga.teacher_reading_portal.book.BookRepository;
import com.njenga.teacher_reading_portal.common.NotFoundException;
import com.njenga.teacher_reading_portal.user.User;
import com.njenga.teacher_reading_portal.user.UserRepository;
import com.njenga.teacher_reading_portal.user.UserRole;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public AssignmentResponse createAssignment(CreateAssignmentRequest request) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));

        User student = userRepository.findById(request.studentId())
                .orElseThrow(() -> new NotFoundException("Student not found"));

        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("Selected user is not a student");
        }

        User teacher = userRepository.findByEmail("teacher@example.com")
                .orElseThrow(() -> new NotFoundException("Demo teacher not found"));

        Assignment assignment = Assignment.builder()
                .book(book)
                .teacher(teacher)
                .student(student)
                .dueDate(request.dueDate())
                .status(AssignmentStatus.NOT_STARTED)
                .minutesRead(0)
                .build();

        Assignment savedAssignment = assignmentRepository.save(assignment);

        return AssignmentResponse.from(savedAssignment);
    }

    public List<AssignmentResponse> getTeacherAssignments() {
        User teacher = userRepository.findByEmail("teacher@example.com")
                .orElseThrow(() -> new NotFoundException("Demo teacher not found"));

        return assignmentRepository.findByTeacher(teacher)
                .stream()
                .map(AssignmentResponse::from)
                .toList();
    }

    public List<AssignmentResponse> getStudentAssignments() {
        User student = userRepository.findByEmail("student@example.com")
                .orElseThrow(() -> new NotFoundException("Demo student not found"));

        return assignmentRepository.findByStudent(student)
                .stream()
                .map(AssignmentResponse::from)
                .toList();
    }

    public AssignmentResponse updateProgress(
            Long assignmentId,
            UpdateAssignmentProgressRequest request
    ) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));

        assignment.setStatus(request.status());
        assignment.setMinutesRead(request.minutesRead());

        Assignment saved = assignmentRepository.save(assignment);

        return AssignmentResponse.from(saved);
    }
}
