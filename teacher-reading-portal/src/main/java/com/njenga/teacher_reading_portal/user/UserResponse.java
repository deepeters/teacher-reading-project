package com.njenga.teacher_reading_portal.user;

public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
