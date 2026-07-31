// ========================================
// EMPLOYEE TYPES
// ========================================

// Lighter department reference (as returned by API)
export interface EmployeeDepartment {
  id: number
  name: string
  code: string
}

// Lighter warehouse reference (as returned by API)
export interface EmployeeWarehouse {
  id: number
  name: string
  code: string
}

// Lighter employee reference for manager (as returned by API)
export interface EmployeeManager {
  id: number
  firstName: string
  lastName: string
  email?: string
  employeeCode?: string
}

// System user reference
export interface EmployeeUser {
  id: number
  username: string
  role?: string
}

export interface Employee {
  id: number
  employeeCode: string
  firstName: string
  lastName: string
  email?: string
  phone?: string
  address?: string
  city?: string
  state?: string
  country?: string
  postalCode?: string
  department?: EmployeeDepartment
  warehouse?: EmployeeWarehouse
  manager?: EmployeeManager | null
  hireDate: string
  terminationDate?: string
  jobTitle?: string
  salary?: number
  isActive: boolean
  hasSystemAccess?: boolean
  user?: EmployeeUser
  createdAt: string
  updatedAt?: string
  version?: number
}

export interface EmployeeFormData {
  employeeCode: string
  firstName: string
  lastName: string
  email?: string
  phone?: string
  address?: string
  city?: string
  state?: string
  country?: string
  postalCode?: string
  departmentId?: number
  warehouseId?: number
  managerId?: number
  hireDate: string
  jobTitle?: string
  salary?: number
  isActive?: boolean
}
