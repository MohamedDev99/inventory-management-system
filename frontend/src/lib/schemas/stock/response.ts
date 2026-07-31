import { z } from "zod"

// ========================================
// STOCK RESPONSE SCHEMAS
// ========================================

// Stock transfer response
export const stockTransferResponseSchema = z.object({
  movementId: z.number(),
  productId: z.number(),
  fromWarehouse: z.object({
    id: z.number(),
    name: z.string(),
    newQuantity: z.number(),
  }),
  toWarehouse: z.object({
    id: z.number(),
    name: z.string(),
    newQuantity: z.number(),
  }),
  quantityTransferred: z.number(),
  movementDate: z.string().datetime(),
})

export type StockTransferResponse = z.infer<typeof stockTransferResponseSchema>

// Stock adjustment response
export const stockAdjustmentResponseSchema = z.object({
  id: z.number(),
  productId: z.number(),
  warehouseId: z.number(),
  quantityBefore: z.number(),
  quantityAfter: z.number(),
  quantityChange: z.number(),
  adjustmentType: z.enum(["ADD", "REMOVE", "SET"]),
  reason: z.string(),
  status: z.enum(["PENDING", "APPROVED", "REJECTED"]),
  createdAt: z.string().datetime(),
})

export type StockAdjustmentResponse = z.infer<typeof stockAdjustmentResponseSchema>
