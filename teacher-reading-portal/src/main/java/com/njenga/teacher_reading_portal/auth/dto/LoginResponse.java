package com.njenga.teacher_reading_portal.auth.dto;

import com.njenga.teacher_reading_portal.user.dto.UserResponse;

public record LoginResponse(
        String token,
        UserResponse user
) {
}
