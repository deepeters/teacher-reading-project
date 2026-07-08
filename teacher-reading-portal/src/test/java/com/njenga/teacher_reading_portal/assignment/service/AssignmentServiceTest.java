package com.njenga.teacher_reading_portal.assignment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    private static final LocalDate DUE_DATE = LocalDate.of(2026, 7, 20);
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 7, 8, 10, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 7, 8, 10, 5);

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private AssignmentService assignmentService;

    @Test
    void createAssignmentSavesAssignmentForCurrentTeacherAndSelectedStudent() {
        Book book = book();
        User teacher = teacher();
        User student = student();
        CreateAssignmentRequest request = new CreateAssignmentRequest(1L, 2L, DUE_DATE);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(2L)).thenReturn(Optional.of(student));
        when(authenticationFacade.getCurrentUser()).thenReturn(teacher);
        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> {
            Assignment assignment = invocation.getArgument(0);
            assignment.setId(10L);
            assignment.setCreatedAt(CREATED_AT);
            assignment.setUpdatedAt(UPDATED_AT);
            return assignment;
        });

        AssignmentResponse response = assignmentService.createAssignment(request);

        ArgumentCaptor<Assignment> captor = ArgumentCaptor.forClass(Assignment.class);
        verify(assignmentRepository).save(captor.capture());
        Assignment saved = captor.getValue();

        assertThat(saved.getBook()).isEqualTo(book);
        assertThat(saved.getTeacher()).isEqualTo(teacher);
        assertThat(saved.getStudent()).isEqualTo(student);
        assertThat(saved.getDueDate()).isEqualTo(DUE_DATE);
        assertThat(saved.getStatus()).isEqualTo(AssignmentStatus.NOT_STARTED);
        assertThat(saved.getMinutesRead()).isZero();

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.bookTitle()).isEqualTo("The Curious Forest");
        assertThat(response.teacherName()).isEqualTo("Teacher Demo");
        assertThat(response.studentName()).isEqualTo("Student Demo");
        assertThat(response.status()).isEqualTo(AssignmentStatus.NOT_STARTED);
        assertThat(response.minutesRead()).isZero();
    }

    @Test
    void createAssignmentThrowsWhenCurrentUserIsNotATeacher() {
        User student = student();
        CreateAssignmentRequest request = new CreateAssignmentRequest(1L, 2L, DUE_DATE);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(student));
        when(authenticationFacade.getCurrentUser()).thenReturn(student);

        assertThatThrownBy(() -> assignmentService.createAssignment(request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only teachers can create assignments");

        verify(assignmentRepository, never()).save(any());
    }

    @Test
    void createAssignmentThrowsWhenBookDoesNotExist() {
        CreateAssignmentRequest request = new CreateAssignmentRequest(99L, 2L, DUE_DATE);
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentService.createAssignment(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Book not found");

        verify(assignmentRepository, never()).save(any());
    }

    @Test
    void createAssignmentThrowsWhenSelectedUserIsNotAStudent() {
        CreateAssignmentRequest request = new CreateAssignmentRequest(1L, 1L, DUE_DATE);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(teacher()));

        assertThatThrownBy(() -> assignmentService.createAssignment(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Selected user is not a student");

        verify(assignmentRepository, never()).save(any());
    }

    @Test
    void getTeacherAssignmentsReturnsAssignmentsForCurrentTeacher() {
        User teacher = teacher();
        Assignment assignment = assignment(100L, teacher, student());

        when(authenticationFacade.getCurrentUser()).thenReturn(teacher);
        when(assignmentRepository.findByTeacher(teacher)).thenReturn(List.of(assignment));

        List<AssignmentResponse> responses = assignmentService.getTeacherAssignments();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().id()).isEqualTo(100L);
        assertThat(responses.getFirst().teacherName()).isEqualTo("Teacher Demo");
        verify(assignmentRepository).findByTeacher(teacher);
    }

    @Test
    void getTeacherAssignmentsThrowsWhenCurrentUserIsNotTeacher() {
        when(authenticationFacade.getCurrentUser()).thenReturn(student());

        assertThatThrownBy(() -> assignmentService.getTeacherAssignments())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only teachers can view teacher assignments");
    }

    @Test
    void getStudentAssignmentsReturnsAssignmentsForCurrentStudent() {
        User student = student();
        Assignment assignment = assignment(101L, teacher(), student);

        when(authenticationFacade.getCurrentUser()).thenReturn(student);
        when(assignmentRepository.findByStudent(student)).thenReturn(List.of(assignment));

        List<AssignmentResponse> responses = assignmentService.getStudentAssignments();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().id()).isEqualTo(101L);
        assertThat(responses.getFirst().studentName()).isEqualTo("Student Demo");
        verify(assignmentRepository).findByStudent(student);
    }

    @Test
    void getStudentAssignmentsThrowsWhenCurrentUserIsNotStudent() {
        when(authenticationFacade.getCurrentUser()).thenReturn(teacher());

        assertThatThrownBy(() -> assignmentService.getStudentAssignments())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only students can view student assignments");
    }

    @Test
    void updateProgressUpdatesStatusAndMinutesRead() {
        User student = student();
        Assignment assignment = assignment(100L, teacher(), student);
        UpdateAssignmentProgressRequest request = new UpdateAssignmentProgressRequest(
                AssignmentStatus.COMPLETED,
                45
        );

        when(assignmentRepository.findById(100L)).thenReturn(Optional.of(assignment));
        when(authenticationFacade.getCurrentUser()).thenReturn(student);
        when(assignmentRepository.save(assignment)).thenAnswer(invocation -> invocation.getArgument(0));

        AssignmentResponse response = assignmentService.updateProgress(100L, request);

        assertThat(assignment.getStatus()).isEqualTo(AssignmentStatus.COMPLETED);
        assertThat(assignment.getMinutesRead()).isEqualTo(45);
        assertThat(response.status()).isEqualTo(AssignmentStatus.COMPLETED);
        assertThat(response.minutesRead()).isEqualTo(45);
        verify(assignmentRepository).save(assignment);
    }

    @Test
    void updateProgressThrowsWhenCurrentUserIsNotStudent() {
        Assignment assignment = assignment(100L, teacher(), student());
        UpdateAssignmentProgressRequest request = new UpdateAssignmentProgressRequest(
                AssignmentStatus.COMPLETED,
                45
        );

        when(assignmentRepository.findById(100L)).thenReturn(Optional.of(assignment));
        when(authenticationFacade.getCurrentUser()).thenReturn(teacher());

        assertThatThrownBy(() -> assignmentService.updateProgress(100L, request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only students can update reading progress");

        verify(assignmentRepository, never()).save(any());
    }

    @Test
    void updateProgressThrowsWhenAssignmentBelongsToAnotherStudent() {
        User currentStudent = student();
        User otherStudent = User.builder()
                .id(3L)
                .name("Alice Student")
                .email("alice@example.com")
                .password("encoded-password")
                .role(UserRole.STUDENT)
                .build();
        Assignment assignment = assignment(100L, teacher(), otherStudent);
        UpdateAssignmentProgressRequest request = new UpdateAssignmentProgressRequest(
                AssignmentStatus.COMPLETED,
                45
        );

        when(assignmentRepository.findById(100L)).thenReturn(Optional.of(assignment));
        when(authenticationFacade.getCurrentUser()).thenReturn(currentStudent);

        assertThatThrownBy(() -> assignmentService.updateProgress(100L, request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Students can only update their own assignments");

        verify(assignmentRepository, never()).save(any());
    }

    @Test
    void updateProgressThrowsWhenAssignmentDoesNotExist() {
        UpdateAssignmentProgressRequest request = new UpdateAssignmentProgressRequest(
                AssignmentStatus.IN_PROGRESS,
                15
        );

        when(assignmentRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentService.updateProgress(404L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Assignment not found");

        verify(assignmentRepository, never()).save(any());
    }

    private Assignment assignment(Long id, User teacher, User student) {
        return Assignment.builder()
                .id(id)
                .book(book())
                .teacher(teacher)
                .student(student)
                .dueDate(DUE_DATE)
                .status(AssignmentStatus.NOT_STARTED)
                .minutesRead(0)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    private Book book() {
        return Book.builder()
                .id(1L)
                .title("The Curious Forest")
                .author("Maya Stone")
                .description("A short story about curiosity.")
                .content("Book content")
                .build();
    }

    private User teacher() {
        return User.builder()
                .id(1L)
                .name("Teacher Demo")
                .email("teacher@example.com")
                .password("encoded-password")
                .role(UserRole.TEACHER)
                .build();
    }

    private User student() {
        return User.builder()
                .id(2L)
                .name("Student Demo")
                .email("student@example.com")
                .password("encoded-password")
                .role(UserRole.STUDENT)
                .build();
    }
}
