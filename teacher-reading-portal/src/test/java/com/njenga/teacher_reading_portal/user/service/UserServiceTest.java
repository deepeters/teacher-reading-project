package com.njenga.teacher_reading_portal.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.njenga.teacher_reading_portal.user.User;
import com.njenga.teacher_reading_portal.user.UserRepository;
import com.njenga.teacher_reading_portal.user.UserRole;
import com.njenga.teacher_reading_portal.user.dto.UserResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getStudentsReturnsMappedStudentUsers() {
        User student = User.builder()
                .id(2L)
                .name("Student Demo")
                .email("student@example.com")
                .password("encoded-password")
                .role(UserRole.STUDENT)
                .build();

        when(userRepository.findByRole(UserRole.STUDENT)).thenReturn(List.of(student));

        List<UserResponse> responses = userService.getStudents();

        assertThat(responses).containsExactly(new UserResponse(
                2L,
                "Student Demo",
                "student@example.com",
                UserRole.STUDENT
        ));
        verify(userRepository).findByRole(UserRole.STUDENT);
    }
}
