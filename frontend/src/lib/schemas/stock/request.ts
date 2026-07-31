import { z } from "zod"

// ========================================
// STOCK REQUEST SCHEMAS (Transfer & Adjustment)
// ========================================

// Stock transfer request
export const stockTransferRequestSchema = z.object({
  productId: z.number().min(1, "Product is required"),
  fromWarehouseId: z.number().min(1, "Source warehouse is required"),
  toWarehouseId: z.number().min(1, "Destination warehouse is required"),
  quantity: z.number().min(1, "Quantity must be at least 1"),
  reason: z.string().optional(),
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
})

export type StockAdjustmentRequest = z.infer<typeof stockAdjustmentRequestSchema>

// Add inventory item (initial stock)
export const inventoryRequestSchema = z.object({
  productId: z.number().min(1, "Product is required"),
  warehouseId: z.number().min(1, "Warehouse is required"),
  quantity: z.number().min(1, "Quantity must be at least 1"),
})

export type InventoryRequest = z.infer<typeof inventoryRequestSchema>
