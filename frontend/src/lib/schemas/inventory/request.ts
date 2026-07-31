import { z } from "zod"

// ========================================
// INVENTORY REQUEST SCHEMAS
// ========================================

// Stock transfer request
export const stockTransferRequestSchema = z.object({
  productId: z.number().min(1, "Product is required"),
  fromWarehouseId: z.number().min(1, "Source warehouse is required"),
  toWarehouseId: z.number().min(1, "Destination warehouse is required"),
  quantity: z.number().min(1, "Quantity must be at least 1"),
  reason: z.string().optional(),
  performedBy: z.number().optional(),
})

export type StockTransferRequest = z.infer<typeof stockTransferRequestSchema>

// Stock adjustment request
export const stockAdjustmentRequestSchema = z.object({
  productId: z.number().min(1, "Product is required"),
  warehouseId: z.number().min(1, "Warehouse is required"),
  quantityChange: z.number(),
  adjustmentType: z.enum(["ADD", "REMOVE", "CORRECTION"]),
  reason: z.enum(["DAMAGED", "EXPIRED", "THEFT", "COUNT_ERROR", "RETURN", "OTHER"]),
  notes: z.string().optional(),
  performedBy: z.number().optional(),
})

export type StockAdjustmentRequest = z.infer<typeof stockAdjustmentRequestSchema>

// Stock receive request (from purchase order)
export const stockReceiveItemSchema = z.object({
  productId: z.number(),
  quantityReceived: z.number().min(1, "Quantity must be at least 1"),
  locationCode: z.string().optional(),
})

export const stockReceiveRequestSchema = z.object({
  purchaseOrderId: z.number().min(1, "Purchase order is required"),
  warehouseId: z.number().min(1, "Warehouse is required"),
  performedBy: z.number().optional(),
  receivedDate: z.string().optional(),
  notes: z.string().optional(),
  items: z.array(stockReceiveItemSchema).min(1, "At least one item is required"),
})

export type StockReceiveRequest = z.infer<typeof stockReceiveRequestSchema>

// Inventory filter params
export const inventoryFilterParamsSchema = z.object({
  warehouseId: z.number().optional(),
  productId: z.number().optional(),
  lowStock: z.boolean().optional(),
  search: z.string().optional(),
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
  sort: z.string().optional(),
})

export type InventoryFilterParams = z.infer<typeof inventoryFilterParamsSchema>

// Inventory movement filter params
export const inventoryMovementFilterParamsSchema = z.object({
  productId: z.number().optional(),
  warehouseId: z.number().optional(),
  movementType: z.enum(["TRANSFER", "ADJUSTMENT", "PURCHASE_RECEIPT", "SALE"]).optional(),
  startDate: z.string().datetime().optional(),
  endDate: z.string().datetime().optional(),
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
  sort: z.string().optional(),
})

export type InventoryMovementFilterParams = z.infer<typeof inventoryMovementFilterParamsSchema>

// Inventory valuation params
export const inventoryValuationParamsSchema = z.object({
  warehouseId: z.number().optional(),
  categoryId: z.number().optional(),
  valuationType: z.enum(["COST", "RETAIL"]).optional(),
})

export type InventoryValuationParams = z.infer<typeof inventoryValuationParamsSchema>
