import { z } from "zod"

// ========================================
// WAREHOUSE REQUEST SCHEMAS
// ========================================

// Create warehouse request
export const createWarehouseRequestSchema = z.object({
  name: z.string().min(1, "Warehouse name is required"),
  code: z
    .string()
    .min(1, "Warehouse code is required")
    .regex(/^[A-Z0-9\-_]+$/, "Code must contain only uppercase letters, digits, hyphens, and underscores"),
  address: z.string().optional(),
  city: z.string().optional(),
  state: z.string().optional(),
  country: z.string().optional(),
  postalCode: z.string().optional(),
  managerId: z.number().optional(),
  capacity: z.number().min(1, "Capacity is required"),
  contactPerson: z.string().optional(),
  phone: z.string().optional(),
  email: z.string().email("Invalid email").optional().or(z.literal("")),
  isActive: z.boolean().optional(),
})

export type CreateWarehouseRequest = z.infer<typeof createWarehouseRequestSchema>

// Update warehouse request
export const updateWarehouseRequestSchema = createWarehouseRequestSchema.partial().refine(
  (data) => Object.keys(data).length > 0,
  { message: "At least one field is required" }
)

export type UpdateWarehouseRequest = z.infer<typeof updateWarehouseRequestSchema>

// Warehouse filter params
export const warehouseFilterParamsSchema = z.object({
  search: z.string().optional(),
  isActive: z.boolean().optional(),
  city: z.string().optional(),
  country: z.string().optional(),
  state: z.string().optional(),
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
  sort: z.string().optional(),
})

export type WarehouseFilterParams = z.infer<typeof warehouseFilterParamsSchema>
