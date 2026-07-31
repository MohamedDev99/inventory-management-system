import { z } from "zod"

// ========================================
// PURCHASE ORDER REQUEST SCHEMAS
// ========================================

// Purchase order item
export const purchaseOrderItemRequestSchema = z.object({
  productId: z.number(),
  quantityOrdered: z.number().min(1, "Quantity must be at least 1"),
  unitPrice: z.number().min(0),
})

// Create purchase order request
export const createPurchaseOrderRequestSchema = z.object({
  supplierId: z.number().min(1, "Supplier is required"),
  warehouseId: z.number().min(1, "Warehouse is required"),
  orderDate: z.string().optional(),
  expectedDeliveryDate: z.string().optional(),
  taxAmount: z.number().min(0).optional(),
  discountAmount: z.number().min(0).optional(),
  notes: z.string().optional(),
  items: z.array(purchaseOrderItemRequestSchema).min(1, "At least one item is required"),
})

export type CreatePurchaseOrderRequest = z.infer<typeof createPurchaseOrderRequestSchema>

// Update purchase order request
export const updatePurchaseOrderRequestSchema = createPurchaseOrderRequestSchema

export type UpdatePurchaseOrderRequest = z.infer<typeof updatePurchaseOrderRequestSchema>

// Receive purchase order item
export const receivePurchaseOrderItemSchema = z.object({
  itemId: z.number(),
  quantityReceived: z.number().min(1, "Quantity must be at least 1"),
})

// Receive purchase order request
export const receivePurchaseOrderRequestSchema = z.object({
  actualDeliveryDate: z.string().optional(),
  notes: z.string().optional(),
  items: z.array(receivePurchaseOrderItemSchema).min(1, "At least one item is required"),
})

export type ReceivePurchaseOrderRequest = z.infer<typeof receivePurchaseOrderRequestSchema>

// Reject/Cancel reason request
export const orderReasonRequestSchema = z.object({
  reason: z.string().min(1, "Reason is required"),
})

export type OrderReasonRequest = z.infer<typeof orderReasonRequestSchema>

// Purchase order filter params
export const purchaseOrderFilterParamsSchema = z.object({
  search: z.string().optional(),
  supplierId: z.number().optional(),
  warehouseId: z.number().optional(),
  status: z.enum(["DRAFT", "SUBMITTED", "APPROVED", "RECEIVED", "CANCELLED"]).optional(),
  createdBy: z.number().optional(),
  startDate: z.string().optional(),
  endDate: z.string().optional(),
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
  sort: z.string().optional(),
})

export type PurchaseOrderFilterParams = z.infer<typeof purchaseOrderFilterParamsSchema>
