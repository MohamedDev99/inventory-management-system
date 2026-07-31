import { z } from "zod"

// ========================================
// WAREHOUSE RESPONSE SCHEMAS
// ========================================

// Manager reference
export const warehouseManagerSchema = z.object({
  id: z.number(),
  username: z.string(),
  email: z.string(),
})

export type WarehouseManager = z.infer<typeof warehouseManagerSchema>

// Warehouse response
export const warehouseSchema = z.object({
  id: z.number(),
  name: z.string(),
  code: z.string(),
  address: z.string().optional(),
  city: z.string().optional(),
  state: z.string().optional(),
  country: z.string().optional(),
  postalCode: z.string().optional(),
  fullAddress: z.string().optional(),
  manager: warehouseManagerSchema.optional(),
  capacity: z.number(),
  isActive: z.boolean(),
  totalProductTypes: z.number().optional(),
  totalStockUnits: z.number().optional(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional(),
  version: z.number().optional(),
})

export type Warehouse = z.infer<typeof warehouseSchema>

// Warehouse stats
export const warehouseStatsSchema = z.object({
  warehouseId: z.number(),
  warehouseName: z.string(),
  totalProducts: z.number(),
  totalUnits: z.number(),
  totalValue: z.number(),
  lowStockProducts: z.number(),
  outOfStockProducts: z.number(),
  capacity: z.number(),
  capacityUtilization: z.number(),
})

export type WarehouseStats = z.infer<typeof warehouseStatsSchema>

// Paginated warehouse list
export const warehouseListResponseSchema = z.object({
  content: z.array(warehouseSchema),
  totalElements: z.number(),
  totalPages: z.number(),
  size: z.number(),
  number: z.number(),
})

export type WarehouseListResponse = z.infer<typeof warehouseListResponseSchema>
