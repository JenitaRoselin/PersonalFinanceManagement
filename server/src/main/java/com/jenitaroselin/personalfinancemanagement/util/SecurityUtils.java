package com.jenitaroselin.personalfinancemanagement.util;

import com.jenitaroselin.personalfinancemanagement.exception.UnauthorizedException;
import com.jenitaroselin.personalfinancemanagement.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) authentication.getPrincipal();
        }
        throw new UnauthorizedException("User not authenticated");
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }
}
