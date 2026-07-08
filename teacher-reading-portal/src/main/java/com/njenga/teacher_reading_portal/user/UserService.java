package com.njenga.teacher_reading_portal.user;

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
