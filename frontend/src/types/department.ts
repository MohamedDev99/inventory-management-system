// ========================================
// DEPARTMENT TYPES
// ========================================

// Lighter manager reference (as returned by API)
export interface DepartmentManager {
  id: number
  firstName: string
  lastName: string
  email?: string
}

// Lighter warehouse reference (as returned by API)
export interface DepartmentWarehouse {
  id: number
  name: string
  code: string
}

export interface Department {
  id: number
  name: string
  code: string
  manager?: DepartmentManager
  email?: string
  phone?: string
  warehouse?: DepartmentWarehouse
  isActive: boolean
  createdAt: string
  updatedAt?: string
  version?: number
}

export interface DepartmentFormData {
  name: string
  code: string
  managerId?: number
  warehouseId?: number
  email?: string
  phone?: string
  isActive?: boolean
}
