package com.moeware.ims.exception.staff.department;

/**
 * Exception thrown when a department is not found
 *
 * @author MoeWare Team
 */
public class DepartmentNotFoundException extends RuntimeException {

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

    public DepartmentNotFoundException(String fieldName, Object fieldValue) {
        super(String.format("Department not found with %s: '%s'", fieldName, fieldValue));
        this.departmentId = null;
        this.departmentCode = null;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }
}