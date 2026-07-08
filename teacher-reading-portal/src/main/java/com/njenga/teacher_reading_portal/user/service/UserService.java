package com.njenga.teacher_reading_portal.user.service;

import com.njenga.teacher_reading_portal.user.UserRepository;
import com.njenga.teacher_reading_portal.user.UserRole;
import com.njenga.teacher_reading_portal.user.dto.UserResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getStudents() {
        return userRepository.findByRole(UserRole.STUDENT)
                .stream()
                .map(UserResponse::from)
                .toList();
    }
}
