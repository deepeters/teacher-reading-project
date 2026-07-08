package com.njenga.teacher_reading_portal.user.controller;

import com.njenga.teacher_reading_portal.user.dto.UserResponse;
import com.njenga.teacher_reading_portal.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/students")
    public List<UserResponse> getStudents() {
        return userService.getStudents();
    }
}
