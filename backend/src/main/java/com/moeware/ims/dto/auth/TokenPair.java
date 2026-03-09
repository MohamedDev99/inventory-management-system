package com.moeware.ims.dto.auth;

/**
 * Carries the raw JWT access and refresh token strings from the service layer
 * to the controller, which sets them as HttpOnly cookies on the HTTP response.
 *
 * <p>
 * This record is an internal transport object and is <b>never</b> serialized
 * to JSON or returned to the client directly.
 * </p>
 *
 * @author MoeWare Team
 */
public record TokenPair(String accessToken, String refreshToken) {
}