package com.moeware.ims.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.moeware.ims.service.CookieService;
import com.moeware.ims.service.JwtService;
import com.moeware.ims.service.TokenBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT Authentication Filter.
 *
 * <p>
 * Token resolution order (first non-null wins):
 * </p>
 * <ol>
 * <li>HttpOnly cookie {@code access_token} — used by browser clients</li>
 * <li>{@code Authorization: Bearer <token>} header — used by API/Postman
 * clients</li>
 * </ol>
 *
 * <p>
 * After resolving the token the filter:
 * </p>
 * <ol>
 * <li>Checks the Redis blacklist (rejects logged-out tokens)</li>
 * <li>Validates the token signature and expiry</li>
 * <li>Sets the Spring Security authentication context on success</li>
 * </ol>
 *
 * @author MoeWare Team
 * @version 1.2
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CookieService cookieService;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // ── 1. Resolve token: cookie first, then Authorization header ──────────
        String jwt = resolveToken(request);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // ── 2. Reject blacklisted (logged-out) tokens ──────────────────────
            if (tokenBlacklistService.isBlacklisted(jwt)) {
                log.warn("Rejected blacklisted token — URI: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            // ── 3. Authenticate if not already done ───────────────────────────
            String username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authenticated user '{}' via {}", username, resolveSource(request));
                }
            }

        } catch (Exception e) {
            log.error("JWT authentication error for URI {}: {}", request.getRequestURI(), e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    // ==========================================
    // Private Helpers
    // ==========================================

    /**
     * Resolve the JWT string from the request.
     * Cookie takes priority over the Authorization header so that browser
     * clients don't need to manually attach tokens.
     *
     * @return raw token string, or {@code null} if absent in both locations
     */
    private String resolveToken(HttpServletRequest request) {
        // Priority 1: HttpOnly cookie (browser clients)
        String fromCookie = cookieService.extractAccessToken(request);
        if (fromCookie != null && !fromCookie.isBlank()) {
            return fromCookie;
        }

        // Priority 2: Authorization header (API / Postman clients)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    /**
     * Helper for debug logging — identifies which token source was used.
     */
    private String resolveSource(HttpServletRequest request) {
        String fromCookie = cookieService.extractAccessToken(request);
        return (fromCookie != null && !fromCookie.isBlank()) ? "cookie" : "Authorization header";
    }
}