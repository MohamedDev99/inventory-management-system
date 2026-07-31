import { z } from "zod"

// ========================================
// EMPLOYEE REQUEST SCHEMAS
// ========================================

// Create employee request
export const createEmployeeRequestSchema = z.object({
  employeeCode: z.string().min(1, "Employee code is required"),
  firstName: z.string().min(1, "First name is required"),
  lastName: z.string().min(1, "Last name is required"),
  email: z.string().email("Invalid email").optional().or(z.literal("")),
  phone: z.string().optional(),
  address: z.string().optional(),
  city: z.string().optional(),
  state: z.string().optional(),
  country: z.string().optional(),
  postalCode: z.string().optional(),
  departmentId: z.number().optional(),
  warehouseId: z.number().optional(),
  managerId: z.number().optional(),
  hireDate: z.string().min(1, "Hire date is required"),
  jobTitle: z.string().optional(),
  salary: z.number().min(0).optional(),
  isActive: z.boolean().optional(),
})

export type CreateEmployeeRequest = z.infer<typeof createEmployeeRequestSchema>

// Update employee request
export const updateEmployeeRequestSchema = createEmployeeRequestSchema.partial().refine(
  (data) => Object.keys(data).length > 0,
  { message: "At least one field is required" }
)

export type UpdateEmployeeRequest = z.infer<typeof updateEmployeeRequestSchema>

// Employee filter params
export const employeeFilterParamsSchema = z.object({
  search: z.string().optional(),
  departmentId: z.number().optional(),
  warehouseId: z.number().optional(),
  managerId: z.number().optional(),
  isActive: z.boolean().optional(),
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
  sort: z.string().optional(),
})

export type EmployeeFilterParams = z.infer<typeof employeeFilterParamsSchema>
