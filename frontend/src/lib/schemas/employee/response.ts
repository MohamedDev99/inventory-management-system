import { z } from "zod"

// ========================================
// EMPLOYEE RESPONSE SCHEMAS
// ========================================

// Department reference in employee
export const employeeDepartmentSchema = z.object({
  id: z.number(),
  name: z.string(),
  code: z.string(),
})

export type EmployeeDepartment = z.infer<typeof employeeDepartmentSchema>

// Warehouse reference in employee
export const employeeWarehouseSchema = z.object({
  id: z.number(),
  name: z.string(),
  code: z.string(),
})

export type EmployeeWarehouse = z.infer<typeof employeeWarehouseSchema>

// Manager reference in employee
export const employeeManagerSchema = z.object({
  id: z.number(),
  firstName: z.string(),
  lastName: z.string(),
  email: z.string().email().optional(),
})

export type EmployeeManager = z.infer<typeof employeeManagerSchema>

// Employee response
export const employeeSchema = z.object({
  id: z.number(),
  employeeCode: z.string(),
  firstName: z.string(),
  lastName: z.string(),
  email: z.string().email().optional(),
  phone: z.string().optional(),
  address: z.string().optional(),
  city: z.string().optional(),
  state: z.string().optional(),
  country: z.string().optional(),
  postalCode: z.string().optional(),
  department: employeeDepartmentSchema.optional(),
  warehouse: employeeWarehouseSchema.optional(),
  manager: employeeManagerSchema.optional(),
  hireDate: z.string(),
  jobTitle: z.string().optional(),
  salary: z.number().optional(),
  isActive: z.boolean(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional(),
  version: z.number().optional(),
})

export type Employee = z.infer<typeof employeeSchema>

// Employee list item
export const employeeListItemSchema = employeeSchema

// Paginated employee list
export const employeeListResponseSchema = z.object({
  content: z.array(employeeSchema),
  totalElements: z.number(),
  totalPages: z.number(),
  size: z.number(),
  number: z.number(),
})

export type EmployeeListResponse = z.infer<typeof employeeListResponseSchema>
