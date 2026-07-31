import { z } from "zod"

// ========================================
// SALES ORDER RESPONSE SCHEMAS
// ========================================

// Sales order status enum
export const salesOrderStatusEnum = z.enum(["PENDING", "CONFIRMED", "FULFILLED", "SHIPPED", "DELIVERED", "CANCELLED"])

export type SalesOrderStatus = z.infer<typeof salesOrderStatusEnum>

// Customer reference in SO
export const soCustomerSchema = z.object({
  id: z.number(),
  customerCode: z.string(),
  contactName: z.string(),
  email: z.string().email().optional(),
})

export type SoCustomer = z.infer<typeof soCustomerSchema>

// Warehouse reference in SO
export const soWarehouseSchema = z.object({
  id: z.number(),
  name: z.string(),
  code: z.string(),
})

export type SoWarehouse = z.infer<typeof soWarehouseSchema>

// User reference in SO
export const soUserSchema = z.object({
  id: z.number(),
  username: z.string(),
  email: z.string().optional(),
})

export type SoUser = z.infer<typeof soUserSchema>

// Sales order item
export const salesOrderItemSchema = z.object({
  id: z.number(),
  productId: z.number(),
  productSku: z.string(),
  productName: z.string(),
  quantity: z.number(),
  unitPrice: z.number(),
  lineTotal: z.number(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional(),
})

export type SalesOrderItem = z.infer<typeof salesOrderItemSchema>

// Sales order summary (for list)
export const salesOrderSummarySchema = z.object({
  id: z.number(),
  soNumber: z.string(),
  customerId: z.number(),
  customerCode: z.string(),
  customerName: z.string(),
  customerEmail: z.string().email().optional(),
  warehouseId: z.number(),
  warehouseName: z.string(),
  status: salesOrderStatusEnum,
  orderDate: z.string(),
  fulfillmentDate: z.string().nullable().optional(),
  shippingDate: z.string().nullable().optional(),
  deliveryDate: z.string().nullable().optional(),
  itemCount: z.number(),
  subtotal: z.number(),
  taxAmount: z.number(),
  shippingCost: z.number(),
  totalAmount: z.number(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional(),
})

export type SalesOrderSummary = z.infer<typeof salesOrderSummarySchema>

// Full sales order response
export const salesOrderSchema = salesOrderSummarySchema.extend({
  customer: soCustomerSchema,
  customerName: z.string(),
  customerEmail: z.string().email().optional(),
  customerPhone: z.string().optional(),
  shippingAddress: z.string().optional(),
  city: z.string().optional(),
  postalCode: z.string().optional(),
  warehouse: soWarehouseSchema,
  createdByUser: soUserSchema.optional(),
  notes: z.string().optional(),
  itemCount: z.number(),
  items: z.array(salesOrderItemSchema),
  version: z.number().optional(),
  createdBy: z.string().optional(),
  updatedBy: z.string().optional(),
})

export type SalesOrder = z.infer<typeof salesOrderSchema>

// Paginated sales order list
export const salesOrderListResponseSchema = z.object({
  content: z.array(salesOrderSummarySchema),
  totalElements: z.number(),
  totalPages: z.number(),
  size: z.number(),
  number: z.number(),
})

export type SalesOrderListResponse = z.infer<typeof salesOrderListResponseSchema>
