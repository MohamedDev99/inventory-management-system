package com.moeware.ims.exception.staff.employee;

/**
 * Exception thrown when an employee is not found
 *
 * @author MoeWare Team
 */
public class EmployeeNotFoundException extends RuntimeException {

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

    public EmployeeNotFoundException(String fieldName, Object fieldValue) {
        super(String.format("Employee not found with %s: '%s'", fieldName, fieldValue));
        this.employeeId = null;
        this.employeeCode = null;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }
}