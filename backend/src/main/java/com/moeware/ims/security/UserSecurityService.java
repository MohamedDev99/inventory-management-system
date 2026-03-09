package com.moeware.ims.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.moeware.ims.entity.User;
import com.moeware.ims.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Security helper service used in {@code @PreAuthorize} SpEL expressions.
 * <p>
 * Spring exposes beans in SpEL via {@code @beanName}, so expressions like
 * {@code @userSecurityService.isSelf(#id, authentication)} can be used directly
 * in controller annotations.
 * </p>
 *
 * <p>
 * <b>Example usage:</b>
 * </p>
 * 
 * <pre>
 * &#64;PreAuthorize("hasRole('ADMIN') or @userSecurityService.isSelf(#id, authentication)")
 * </pre>
 *
 * @author MoeWare Team
 */
@Service("userSecurityService")
@RequiredArgsConstructor
@Slf4j
public class UserSecurityService {

    private final UserRepository userRepository;

    /**
     * Check whether the currently authenticated principal is the same user as
     * {@code userId}.
     *
     * @param userId         the ID of the resource being accessed
     * @param authentication the current Spring Security authentication context
     * @return {@code true} if the authenticated user's ID matches {@code userId}
     */
    public boolean isSelf(Long userId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        try {
            String username = authentication.getName();
            return userRepository.findByUsername(username)
                    .map(user -> user.getId().equals(userId))
                    .orElse(false);
        } catch (Exception e) {
            log.error("Error checking self-access for userId {}: {}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * Check whether the currently authenticated user is an ADMIN.
     *
     * @param authentication the current Spring Security authentication context
     * @return {@code true} if the user has ROLE_ADMIN
     */
    public boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Check whether the currently authenticated principal is a {@link User} entity.
     * Useful when you need to access full user details inside SpEL expressions.
     *
     * @param authentication the current Spring Security authentication context
     * @return the authenticated {@link User}, or {@code null} if not available
     */
    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return null;
        }
        return user;
    }
}