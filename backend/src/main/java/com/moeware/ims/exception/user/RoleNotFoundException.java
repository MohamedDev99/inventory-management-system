package com.moeware.ims.exception.user;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a role cannot be found by name.
 *
 * @author MoeWare Team
 */
public class RoleNotFoundException extends BaseAppException {

    private final String roleName;

    public RoleNotFoundException(String roleName) {
        super("Role not found: " + roleName);
        this.roleName = roleName;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Role Not Found";
    }

    public String getRoleName() {
        return roleName;
    }
}