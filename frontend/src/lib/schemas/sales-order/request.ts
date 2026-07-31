import { z } from "zod"

// ========================================
// SALES ORDER REQUEST SCHEMAS
// ========================================

// Sales order item
export const salesOrderItemRequestSchema = z.object({
  productId: z.number(),
  quantity: z.number().min(1, "Quantity must be at least 1"),
  unitPrice: z.number().min(0),
  discount: z.number().min(0).optional(),
})

// Create sales order request
export const createSalesOrderRequestSchema = z.object({
  customerId: z.number().min(1, "Customer is required"),
  warehouseId: z.number().min(1, "Warehouse is required"),
  customerName: z.string().min(1, "Customer name is required"),
  customerEmail: z.string().email().optional().or(z.literal("")),
  customerPhone: z.string().optional(),
  shippingAddress: z.string().optional(),
  city: z.string().optional(),
  postalCode: z.string().optional(),
  orderDate: z.string().optional(),
  expectedDeliveryDate: z.string().optional(),
  taxAmount: z.number().min(0).optional(),
  shippingCost: z.number().min(0).optional(),
  notes: z.string().optional(),
  items: z.array(salesOrderItemRequestSchema).min(1, "At least one item is required"),
})

export type CreateSalesOrderRequest = z.infer<typeof createSalesOrderRequestSchema>

// Update sales order request
export const updateSalesOrderRequestSchema = createSalesOrderRequestSchema

export type UpdateSalesOrderRequest = z.infer<typeof updateSalesOrderRequestSchema>

// Sales order cancel reason
export const salesOrderCancelRequestSchema = z.object({
  reason: z.string().min(1, "Reason is required"),
})

export type SalesOrderCancelRequest = z.infer<typeof salesOrderCancelRequestSchema>

// Sales order filter params
export const salesOrderFilterParamsSchema = z.object({
  search: z.string().optional(),
  customerId: z.number().optional(),
  warehouseId: z.number().optional(),
  status: z.enum(["PENDING", "CONFIRMED", "FULFILLED", "SHIPPED", "DELIVERED", "CANCELLED"]).optional(),
  createdBy: z.number().optional(),
  startDate: z.string().optional(),
  endDate: z.string().optional(),
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
  sort: z.string().optional(),
})

export type SalesOrderFilterParams = z.infer<typeof salesOrderFilterParamsSchema>
