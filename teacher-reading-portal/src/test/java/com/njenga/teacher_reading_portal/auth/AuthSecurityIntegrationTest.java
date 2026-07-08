package com.njenga.teacher_reading_portal.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.njenga.teacher_reading_portal.assignment.Assignment;
import com.njenga.teacher_reading_portal.assignment.AssignmentRepository;
import com.njenga.teacher_reading_portal.book.Book;
import com.njenga.teacher_reading_portal.book.BookRepository;
import com.njenga.teacher_reading_portal.user.User;
import com.njenga.teacher_reading_portal.user.UserRepository;
import com.njenga.teacher_reading_portal.user.UserRole;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthSecurityIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Test
    void loginSucceedsWithValidCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "teacher@example.com",
                                  "password": "password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("teacher@example.com"))
                .andExpect(jsonPath("$.user.role").value("TEACHER"));
    }

    @Test
    void loginFailsWithBadPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "teacher@example.com",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void protectedEndpointReturnsUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication required"));
    }

    @Test
    void teacherCannotCallStudentOnlyProgressEndpoint() throws Exception {
        Assignment assignment = assignmentRepository.findAll().getFirst();
        String teacherToken = loginAndGetToken("teacher@example.com", "password");

        mockMvc.perform(patch("/api/assignments/{id}/progress", assignment.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(teacherToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "COMPLETED",
                                  "minutesRead": 90
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Only students can update reading progress"));
    }

    @Test
    void studentCannotCreateAssignments() throws Exception {
        Book book = bookRepository.findAll().getFirst();
        User student = userRepository.findByRole(UserRole.STUDENT).getFirst();
        String studentToken = loginAndGetToken("student@example.com", "password");

        mockMvc.perform(post("/api/assignments")
                        .header(HttpHeaders.AUTHORIZATION, bearer(studentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookId": %d,
                                  "studentId": %d,
                                  "dueDate": "%s"
                                }
                                """.formatted(
                                book.getId(),
                                student.getId(),
                                LocalDate.now().plusDays(7)
                        )))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Only teachers can create assignments"));
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode body = objectMapper.readTree(response);
        String token = body.get("token").asText();

        assertThat(token).isNotBlank();

        return token;
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
