package com.moeware.ims.exception.staff.department;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a department cannot be found by ID or department code.
 *
 * @author MoeWare Team
 */
public class DepartmentNotFoundException extends BaseAppException {

    private final Long departmentId;
    private final String departmentCode;

    public DepartmentNotFoundException(Long id) {
        super(String.format("Department not found with id: '%d'", id));
        this.departmentId = id;
        this.departmentCode = null;
    }

    public DepartmentNotFoundException(String code) {
        super(String.format("Department not found with code: '%s'", code));
        this.departmentId = null;
        this.departmentCode = code;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Department Not Found";
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }
}