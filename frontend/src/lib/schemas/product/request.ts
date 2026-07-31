import { z } from "zod"

// ========================================
// PRODUCT REQUEST SCHEMAS
// ========================================

// Create product request
export const createProductRequestSchema = z.object({
  sku: z
    .string()
    .min(1, "SKU is required")
    .regex(/^[A-Z0-9\-_]+$/, "SKU must contain only uppercase letters, digits, hyphens, and underscores"),
  name: z.string().min(1, "Product name is required"),
  description: z.string().optional(),
  categoryId: z.number().optional(),
  unit: z.string().optional(),
  unitPrice: z.number().min(0, "Unit price must be positive"),
  costPrice: z.number().min(0, "Cost price must be positive"),
  reorderLevel: z.number().min(0).optional(),
  minStockLevel: z.number().min(0).optional(),
  barcode: z.string().optional(),
  imageUrl: z.string().url("Invalid URL").optional().or(z.literal("")),
  isActive: z.boolean().optional(),
})

// Validate minStockLevel <= reorderLevel
export const productStockLevelSchema = createProductRequestSchema.refine(
  (data) => {
    if (data.minStockLevel !== undefined && data.reorderLevel !== undefined) {
      return data.minStockLevel <= data.reorderLevel
    }
    return true
  },
  {
    message: "Minimum stock level must be less than or equal to reorder level",
    path: ["minStockLevel"],
  }
)

export type CreateProductRequest = z.infer<typeof productStockLevelSchema>

// Update product request (all fields optional)
export const updateProductRequestSchema = createProductRequestSchema.partial().refine(
  (data) => Object.keys(data).length > 0,
  {
    message: "At least one field is required",
  }
)

export type UpdateProductRequest = z.infer<typeof updateProductRequestSchema>

// Product filter params
export const productFilterParamsSchema = z.object({
  search: z.string().optional(),
  categoryId: z.number().optional(),
  isActive: z.boolean().optional(),
  minPrice: z.number().optional(),
  maxPrice: z.number().optional(),
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
  sort: z.string().optional(),
})

export type ProductFilterParams = z.infer<typeof productFilterParamsSchema>
