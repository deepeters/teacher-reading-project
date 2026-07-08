package com.njenga.teacher_reading_portal.auth;

import com.njenga.teacher_reading_portal.user.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }

        if (authentication.getPrincipal() instanceof User user) {
            return user;
        }

        throw new AccessDeniedException("Authentication required");
    }
}
