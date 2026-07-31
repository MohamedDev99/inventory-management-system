import { z } from "zod"

// ========================================
// PURCHASE ORDER RESPONSE SCHEMAS
// ========================================

// Purchase order status enum
export const purchaseOrderStatusEnum = z.enum(["DRAFT", "SUBMITTED", "APPROVED", "RECEIVED", "CANCELLED"])

export type PurchaseOrderStatus = z.infer<typeof purchaseOrderStatusEnum>

// Supplier reference in PO
export const poSupplierSchema = z.object({
  id: z.number(),
  name: z.string(),
  code: z.string(),
  contactPerson: z.string().optional(),
  email: z.string().email().optional(),
})

export type PoSupplier = z.infer<typeof poSupplierSchema>

// Warehouse reference in PO
export const poWarehouseSchema = z.object({
  id: z.number(),
  name: z.string(),
  code: z.string(),
})

export type PoWarehouse = z.infer<typeof poWarehouseSchema>

// User reference in PO
export const poUserSchema = z.object({
  id: z.number(),
  username: z.string(),
  email: z.string().optional(),
})

export type PoUser = z.infer<typeof poUserSchema>

// Purchase order item
export const purchaseOrderItemSchema = z.object({
  id: z.number(),
  productId: z.number(),
  productSku: z.string(),
  productName: z.string(),
  quantityOrdered: z.number(),
  quantityReceived: z.number(),
  unitPrice: z.number(),
  lineTotal: z.number(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional(),
})

export type PurchaseOrderItem = z.infer<typeof purchaseOrderItemSchema>

// Purchase order summary (for list)
export const purchaseOrderSummarySchema = z.object({
  id: z.number(),
  poNumber: z.string(),
  supplierId: z.number(),
  supplierName: z.string(),
  supplierCode: z.string(),
  warehouseId: z.number(),
  warehouseName: z.string(),
  status: purchaseOrderStatusEnum,
  orderDate: z.string(),
  expectedDeliveryDate: z.string().nullable().optional(),
  actualDeliveryDate: z.string().nullable().optional(),
  itemCount: z.number(),
  subtotal: z.number(),
  taxAmount: z.number(),
  discountAmount: z.number(),
  totalAmount: z.number(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional(),
})

export type PurchaseOrderSummary = z.infer<typeof purchaseOrderSummarySchema>

// Full purchase order response
export const purchaseOrderSchema = purchaseOrderSummarySchema.extend({
  supplier: poSupplierSchema,
  warehouse: poWarehouseSchema,
  createdByUser: poUserSchema.optional(),
  notes: z.string().optional(),
  itemCount: z.number(),
  items: z.array(purchaseOrderItemSchema),
  version: z.number().optional(),
  createdBy: z.string().optional(),
  updatedBy: z.string().optional(),
})

export type PurchaseOrder = z.infer<typeof purchaseOrderSchema>

// Paginated purchase order list
export const purchaseOrderListResponseSchema = z.object({
  content: z.array(purchaseOrderSummarySchema),
  totalElements: z.number(),
  totalPages: z.number(),
  size: z.number(),
  number: z.number(),
})

export type PurchaseOrderListResponse = z.infer<typeof purchaseOrderListResponseSchema>
