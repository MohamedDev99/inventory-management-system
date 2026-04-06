package com.moeware.ims.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing JWT HttpOnly cookies.
 *
 * <p>
 * Cookie strategy:
 * </p>
 * <ul>
 * <li>{@code access_token} — short-lived JWT access token (default 24h)</li>
 * <li>{@code refresh_token} — long-lived JWT refresh token (default 7d)</li>
 * </ul>
 *
 * <p>
 * Both cookies are:
 * </p>
 * <ul>
 * <li><b>HttpOnly</b> — not accessible via JavaScript (prevents XSS theft)</li>
 * <li><b>Secure</b> — only sent over HTTPS in production</li>
 * <li><b>SameSite=Lax</b> — protects against CSRF while supporting normal
 * navigation</li>
 * <li><b>Path=/api</b> — only sent to API endpoints, not static assets</li>
 * </ul>
 *
 * <p>
 * Required in {@code application.yml}:
 * </p>
 *
 * <pre>
 * app:
 *   cookie:
 *     secure: false          # true in production (HTTPS)
 *     domain:                # leave empty for same-domain; set to .moeware.com for subdomains
 *     access-token-max-age: 86400       # 24h in seconds
 *     refresh-token-max-age: 604800     # 7d  in seconds
 * </pre>
 *
 * @author MoeWare Team
 * @version 1.0
 */
@Service
@Slf4j
public class CookieService {

    public static final String ACCESS_TOKEN_COOKIE = "access_token";
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    @Value("${app.cookie.domain:}")
    private String cookieDomain;

    @Value("${app.cookie.access-token-max-age:86400}")
    private int accessTokenMaxAge; // seconds — default 24h

    @Value("${app.cookie.refresh-token-max-age:604800}")
    private int refreshTokenMaxAge; // seconds — default 7d

    // ==========================================
    // Create Cookies
    // ==========================================

    /**
     * Add the access token as an HttpOnly cookie to the response.
     *
     * @param response    the HTTP response to attach the cookie to
     * @param accessToken the raw JWT access token string
     */
    public void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        ResponseCookie cookie = buildCookie(ACCESS_TOKEN_COOKIE, accessToken, accessTokenMaxAge);
        response.addHeader("Set-Cookie", cookie.toString());
        log.debug("Access token cookie set");
    }

    /**
     * Add the refresh token as an HttpOnly cookie to the response.
     *
     * @param response     the HTTP response to attach the cookie to
     * @param refreshToken the raw JWT refresh token string
     */
    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = buildCookie(REFRESH_TOKEN_COOKIE, refreshToken, refreshTokenMaxAge);
        response.addHeader("Set-Cookie", cookie.toString());
        log.debug("Refresh token cookie set");
    }

    // ==========================================
    // Clear Cookies (logout)
    // ==========================================

    /**
     * Clear both JWT cookies by setting their max-age to 0.
     * Should be called on logout.
     *
     * @param response the HTTP response to attach the cleared cookies to
     */
    public void clearAuthCookies(HttpServletResponse response) {
        ResponseCookie clearAccess = buildCookie(ACCESS_TOKEN_COOKIE, "", 0);
        ResponseCookie clearRefresh = buildCookie(REFRESH_TOKEN_COOKIE, "", 0);
        response.addHeader("Set-Cookie", clearAccess.toString());
        response.addHeader("Set-Cookie", clearRefresh.toString());
        log.debug("Auth cookies cleared");
    }

    // ==========================================
    // Extract Cookies from Request
    // ==========================================

    /**
     * Extract the access token value from the incoming request cookies.
     *
     * @param request the incoming HTTP request
     * @return the access token string, or {@code null} if not present
     */
    public String extractAccessToken(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, ACCESS_TOKEN_COOKIE);
        log.info("Access token cookie: {}", cookie);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * Extract the refresh token value from the incoming request cookies.
     *
     * @param request the incoming HTTP request
     * @return the refresh token string, or {@code null} if not present
     */
    public String extractRefreshToken(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, REFRESH_TOKEN_COOKIE);
        return cookie != null ? cookie.getValue() : null;
    }

    // ==========================================
    // Private Builder
    // ==========================================

    private ResponseCookie buildCookie(String name, String value, int maxAge) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite(secureCookie ? "None" : "Lax")
                .path("/api")
                .maxAge(maxAge);

        // Only set domain if explicitly configured (needed for subdomain sharing)
        if (cookieDomain != null && !cookieDomain.isBlank()) {
            builder.domain(cookieDomain);
        }

        return builder.build();
    }
}