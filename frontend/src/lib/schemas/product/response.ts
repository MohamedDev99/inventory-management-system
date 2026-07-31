import { z } from "zod"

// ========================================
// PRODUCT RESPONSE SCHEMAS
// ========================================

// Category reference in product response
export const productCategorySchema = z.object({
  id: z.number(),
  name: z.string(),
  code: z.string(),
  fullPath: z.string().optional(),
})

export type ProductCategory = z.infer<typeof productCategorySchema>

// Stock status enum
export const stockStatusEnum = z.enum(["IN_STOCK", "LOW_STOCK", "OUT_OF_STOCK"])

export type StockStatus = z.infer<typeof stockStatusEnum>

// Product response
export const productSchema = z.object({
  id: z.number(),
  sku: z.string(),
  name: z.string(),
  description: z.string().optional(),
  category: productCategorySchema.optional(),
  unit: z.string(),
  unitPrice: z.number(),
  costPrice: z.number(),
  profitMargin: z.number().optional(),
  marginPercentage: z.number().optional(),
  reorderLevel: z.number(),
  minStockLevel: z.number().optional(),
  barcode: z.string().optional(),
  imageUrl: z.string().optional(),
  isActive: z.boolean(),
  totalStock: z.number().optional(),
  availableStock: z.number().optional(),
  reservedStock: z.number().optional(),
  stockStatus: stockStatusEnum.optional(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional(),
  version: z.number().optional(),
})

export type Product = z.infer<typeof productSchema>

// Paginated product list
export const productListItemSchema = productSchema

export const productListResponseSchema = z.object({
  content: z.array(productSchema),
  totalElements: z.number(),
  totalPages: z.number(),
  size: z.number(),
  number: z.number(),
})

export type ProductListResponse = z.infer<typeof productListResponseSchema>

// Product stats
export const productStatsSchema = z.object({
  totalProducts: z.number(),
  totalValue: z.number().optional(),
  lowStockCount: z.number().optional(),
  outOfStockCount: z.number().optional(),
})

export type ProductStats = z.infer<typeof productStatsSchema>
