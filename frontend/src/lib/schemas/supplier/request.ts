import { z } from "zod"

// ========================================
// SUPPLIER REQUEST SCHEMAS
// ========================================

// Create supplier request
export const createSupplierRequestSchema = z.object({
  name: z.string().min(1, "Supplier name is required"),
  code: z.string().min(1, "Supplier code is required"),
  contactPerson: z.string().optional(),
  email: z.string().email("Invalid email").optional().or(z.literal("")),
  phone: z.string().optional(),
  address: z.string().optional(),
  city: z.string().optional(),
  country: z.string().optional(),
  paymentTerms: z.string().optional(),
  rating: z.number().min(1).max(5).optional(),
  isActive: z.boolean().optional(),
})

export type CreateSupplierRequest = z.infer<typeof createSupplierRequestSchema>

// Update supplier request
export const updateSupplierRequestSchema = createSupplierRequestSchema.partial().refine(
  (data) => Object.keys(data).length > 0,
  { message: "At least one field is required" }
)

export type UpdateSupplierRequest = z.infer<typeof updateSupplierRequestSchema>

// Supplier filter params
export const supplierFilterParamsSchema = z.object({
  isActive: z.boolean().optional(),
  country: z.string().optional(),
  minRating: z.number().min(1).max(5).optional(),
  maxRating: z.number().min(1).max(5).optional(),
  search: z.string().optional(),
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
  sort: z.string().optional(),
})

export type SupplierFilterParams = z.infer<typeof supplierFilterParamsSchema>
