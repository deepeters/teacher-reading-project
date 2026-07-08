package com.njenga.teacher_reading_portal.assignment.service;

import com.njenga.teacher_reading_portal.assignment.Assignment;
import com.njenga.teacher_reading_portal.assignment.AssignmentRepository;
import com.njenga.teacher_reading_portal.assignment.AssignmentStatus;
import com.njenga.teacher_reading_portal.assignment.dto.AssignmentResponse;
import com.njenga.teacher_reading_portal.assignment.dto.CreateAssignmentRequest;
import com.njenga.teacher_reading_portal.assignment.dto.UpdateAssignmentProgressRequest;
import com.njenga.teacher_reading_portal.auth.AuthenticationFacade;
import com.njenga.teacher_reading_portal.book.Book;
import com.njenga.teacher_reading_portal.book.BookRepository;
import com.njenga.teacher_reading_portal.common.NotFoundException;
import com.njenga.teacher_reading_portal.user.User;
import com.njenga.teacher_reading_portal.user.UserRepository;
import com.njenga.teacher_reading_portal.user.UserRole;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;

    public AssignmentResponse createAssignment(CreateAssignmentRequest request) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));

        User student = userRepository.findById(request.studentId())
                .orElseThrow(() -> new NotFoundException("Student not found"));

        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("Selected user is not a student");
        }

        User teacher = authenticationFacade.getCurrentUser();

        if (teacher.getRole() != UserRole.TEACHER) {
            throw new AccessDeniedException("Only teachers can create assignments");
        }

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
        User teacher = authenticationFacade.getCurrentUser();

        if (teacher.getRole() != UserRole.TEACHER) {
            throw new AccessDeniedException("Only teachers can view teacher assignments");
        }

        return assignmentRepository.findByTeacher(teacher)
                .stream()
                .map(AssignmentResponse::from)
                .toList();
    }

    public List<AssignmentResponse> getStudentAssignments() {
        User student = authenticationFacade.getCurrentUser();

        if (student.getRole() != UserRole.STUDENT) {
            throw new AccessDeniedException("Only students can view student assignments");
        }

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
        User student = authenticationFacade.getCurrentUser();

        if (student.getRole() != UserRole.STUDENT) {
            throw new AccessDeniedException("Only students can update reading progress");
        }

        if (!assignment.getStudent().getId().equals(student.getId())) {
            throw new AccessDeniedException("Students can only update their own assignments");
        }

        assignment.setStatus(request.status());
        assignment.setMinutesRead(request.minutesRead());

        Assignment saved = assignmentRepository.save(assignment);

        return AssignmentResponse.from(saved);
    }
}
