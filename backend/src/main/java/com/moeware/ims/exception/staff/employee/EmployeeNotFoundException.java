package com.moeware.ims.exception.staff.employee;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when an employee cannot be found by ID or employee code.
 *
 * @author MoeWare Team
 */
public class EmployeeNotFoundException extends BaseAppException {

    private final Long employeeId;
    private final String employeeCode;

    public EmployeeNotFoundException(Long id) {
        super(String.format("Employee not found with id: '%d'", id));
        this.employeeId = id;
        this.employeeCode = null;
    }

    public EmployeeNotFoundException(String code) {
        super(String.format("Employee not found with code: '%s'", code));
        this.employeeId = null;
        this.employeeCode = code;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Employee Not Found";
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }
}