import { z } from "zod"

// ========================================
// DEPARTMENT REQUEST SCHEMAS
// ========================================

// Create department request
export const createDepartmentRequestSchema = z.object({
  name: z.string().min(1, "Department name is required"),
  code: z.string().min(1, "Department code is required"),
  managerId: z.number().optional(),
  warehouseId: z.number().optional(),
  email: z.string().email("Invalid email").optional().or(z.literal("")),
  phone: z.string().optional(),
})

export type CreateDepartmentRequest = z.infer<typeof createDepartmentRequestSchema>

// Update department request
export const updateDepartmentRequestSchema = createDepartmentRequestSchema.partial().refine(
  (data) => Object.keys(data).length > 0,
  { message: "At least one field is required" }
)

export type UpdateDepartmentRequest = z.infer<typeof updateDepartmentRequestSchema>

// Department filter params
export const departmentFilterParamsSchema = z.object({
  search: z.string().optional(),
  warehouseId: z.number().optional(),
  isActive: z.boolean().optional(),
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
  sort: z.string().optional(),
})

export type DepartmentFilterParams = z.infer<typeof departmentFilterParamsSchema>
