import { z } from "zod"

// ========================================
// DEPARTMENT RESPONSE SCHEMAS
// ========================================

// Manager reference in department
export const departmentManagerSchema = z.object({
  id: z.number(),
  firstName: z.string(),
  lastName: z.string(),
  email: z.string().email().optional(),
})

export type DepartmentManager = z.infer<typeof departmentManagerSchema>

// Warehouse reference in department
export const departmentWarehouseSchema = z.object({
  id: z.number(),
  name: z.string(),
  code: z.string(),
})

export type DepartmentWarehouse = z.infer<typeof departmentWarehouseSchema>

// Department response
export const departmentSchema = z.object({
  id: z.number(),
  name: z.string(),
  code: z.string(),
  manager: departmentManagerSchema.optional(),
  warehouse: departmentWarehouseSchema.optional(),
  email: z.string().email().optional(),
  phone: z.string().optional(),
  employeeCount: z.number().optional(),
  isActive: z.boolean(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional(),
  version: z.number().optional(),
})

export type Department = z.infer<typeof departmentSchema>

// Paginated department list
export const departmentListResponseSchema = z.object({
  content: z.array(departmentSchema),
  totalElements: z.number(),
  totalPages: z.number(),
  size: z.number(),
  number: z.number(),
})

export type DepartmentListResponse = z.infer<typeof departmentListResponseSchema>
