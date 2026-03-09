package com.moeware.ims.dto.auth;

import com.moeware.ims.dto.user.UserResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response returned after successful authentication (login, register, token
 * refresh).
 *
 * <p>
 * JWT tokens are <b>not</b> included in the JSON body — they are delivered as
 * HttpOnly cookies ({@code access_token} and {@code refresh_token}) set
 * directly
 * on the HTTP response. This prevents JavaScript from accessing the tokens,
 * protecting against XSS attacks.
 * </p>
 *
 * <p>
 * This response body contains only non-sensitive metadata the frontend
 * needs to render the UI immediately after login.
 * </p>
 *
 * @author MoeWare Team
 * @version 1.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Authentication success response. JWT tokens are delivered via HttpOnly cookies, not in this body.")
public class AuthenticationResponse {

    @Schema(description = "Token type — always 'Bearer' (informational)", example = "Bearer")
    private String tokenType;

    @Schema(description = "Access token expiry in seconds (informational — actual expiry enforced server-side)", example = "86400")
    private Long expiresIn;

    @Schema(description = "Authenticated user's profile information")
    private UserResponseDto user;
}