import { z } from "zod"

// ========================================
// INVENTORY RESPONSE SCHEMAS
// ========================================

// Product reference in inventory
export const inventoryProductSchema = z.object({
  id: z.number(),
  sku: z.string(),
  name: z.string(),
  unitPrice: z.number().optional(),
})

export type InventoryProduct = z.infer<typeof inventoryProductSchema>

// Warehouse reference in inventory
export const inventoryWarehouseSchema = z.object({
  id: z.number(),
  name: z.string(),
  code: z.string(),
})

export type InventoryWarehouse = z.infer<typeof inventoryWarehouseSchema>

// Stock status enum
export const stockStatusEnum = z.enum(["NORMAL", "LOW", "OUT_OF_STOCK"])

export type StockStatus = z.infer<typeof stockStatusEnum>

// Inventory item response
export const inventoryItemSchema = z.object({
  id: z.number(),
  product: inventoryProductSchema,
  warehouse: inventoryWarehouseSchema,
  quantity: z.number(),
  locationCode: z.string().optional(),
  reorderLevel: z.number(),
  isLowStock: z.boolean(),
  stockStatus: stockStatusEnum,
  lastStockCheck: z.string().datetime().optional(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional(),
  version: z.number().optional(),
})

export type InventoryItem = z.infer<typeof inventoryItemSchema>

// Inventory movement type enum
export const movementTypeEnum = z.enum(["TRANSFER", "ADJUSTMENT", "PURCHASE_RECEIPT", "SALE"])

export type MovementType = z.infer<typeof movementTypeEnum>

// User reference in movement
export const movementUserSchema = z.object({
  id: z.number(),
  username: z.string(),
})

export type MovementUser = z.infer<typeof movementUserSchema>

// Inventory movement response
export const inventoryMovementSchema = z.object({
  id: z.number(),
  product: inventoryProductSchema,
  fromWarehouse: inventoryWarehouseSchema.optional(),
  toWarehouse: inventoryWarehouseSchema.optional(),
  quantity: z.number(),
  movementType: movementTypeEnum,
  reason: z.string().optional(),
  referenceNumber: z.string().optional(),
  performedBy: movementUserSchema.optional(),
  movementDate: z.string().datetime(),
  createdAt: z.string().datetime(),
})

export type InventoryMovement = z.infer<typeof inventoryMovementSchema>

// Transfer response
export const transferInventoryResponseSchema = z.object({
  movementId: z.number(),
  productId: z.number(),
  fromWarehouse: inventoryWarehouseSchema.extend({
    newQuantity: z.number(),
  }),
  toWarehouse: inventoryWarehouseSchema.extend({
    newQuantity: z.number(),
  }),
  quantityTransferred: z.number(),
  movementDate: z.string().datetime(),
})

export type TransferInventoryResponse = z.infer<typeof transferInventoryResponseSchema>

// Stock adjustment status enum
export const adjustmentStatusEnum = z.enum(["PENDING", "APPROVED", "REJECTED"])

export type AdjustmentStatus = z.infer<typeof adjustmentStatusEnum>

// Stock adjustment response
export const stockAdjustmentResponseSchema = z.object({
  id: z.number(),
  productId: z.number(),
  warehouseId: z.number(),
  quantityBefore: z.number(),
  quantityAfter: z.number(),
  quantityChange: z.number(),
  adjustmentType: z.enum(["ADD", "REMOVE", "CORRECTION"]),
  reason: z.string(),
  status: adjustmentStatusEnum,
  performedBy: movementUserSchema.optional(),
  approvedBy: movementUserSchema.nullable().optional(),
  notes: z.string().optional(),
  createdAt: z.string().datetime(),
})

export type StockAdjustmentResponse = z.infer<typeof stockAdjustmentResponseSchema>

// Inventory valuation by warehouse
export const inventoryValuationWarehouseSchema = z.object({
  warehouseId: z.number(),
  warehouseName: z.string(),
  products: z.number(),
  units: z.number(),
  costValue: z.number(),
  retailValue: z.number(),
})

export type InventoryValuationWarehouse = z.infer<typeof inventoryValuationWarehouseSchema>

// Inventory valuation by category
export const inventoryValuationCategorySchema = z.object({
  categoryId: z.number(),
  categoryName: z.string(),
  costValue: z.number(),
  retailValue: z.number(),
})

export type InventoryValuationCategory = z.infer<typeof inventoryValuationCategorySchema>

// Inventory valuation response
export const inventoryValuationSchema = z.object({
  totalProducts: z.number(),
  totalUnits: z.number(),
  costValue: z.number(),
  retailValue: z.number(),
  potentialProfit: z.number(),
  byWarehouse: z.array(inventoryValuationWarehouseSchema),
  byCategory: z.array(inventoryValuationCategorySchema),
})

export type InventoryValuation = z.infer<typeof inventoryValuationSchema>

// Paginated inventory list
export const inventoryListResponseSchema = z.object({
  content: z.array(inventoryItemSchema),
  page: z.number(),
  size: z.number(),
  totalElements: z.number(),
  totalPages: z.number(),
  first: z.boolean(),
  last: z.boolean(),
})

export type InventoryListResponse = z.infer<typeof inventoryListResponseSchema>
