import { z } from "zod"

// ========================================
// SHIPMENT RESPONSE SCHEMAS
// ========================================

// Shipment status enum
export const shipmentStatusEnum = z.enum(["PENDING", "IN_TRANSIT", "DELIVERED", "CANCELLED"])

export type ShipmentStatus = z.infer<typeof shipmentStatusEnum>

// Sales order reference in shipment
export const shipmentSalesOrderSchema = z.object({
  id: z.number(),
  soNumber: z.string(),
  customerName: z.string(),
})

export type ShipmentSalesOrder = z.infer<typeof shipmentSalesOrderSchema>

// Warehouse reference in shipment
export const shipmentWarehouseSchema = z.object({
  id: z.number(),
  name: z.string(),
  code: z.string(),
})

export type ShipmentWarehouse = z.infer<typeof shipmentWarehouseSchema>

// User reference in shipment
export const shipmentUserSchema = z.object({
  id: z.number(),
  username: z.string(),
  email: z.string().email().optional(),
})

export type ShipmentUser = z.infer<typeof shipmentUserSchema>

// Shipment response
export const shipmentSchema = z.object({
  id: z.number(),
  shipmentNumber: z.string(),
  salesOrder: shipmentSalesOrderSchema,
  shippedFromWarehouse: shipmentWarehouseSchema,
  shippedBy: shipmentUserSchema.optional(),
  carrier: z.string(),
  trackingNumber: z.string().optional(),
  shippingMethod: z.enum(["STANDARD", "EXPRESS", "OVERNIGHT"]).optional(),
  estimatedDeliveryDate: z.string().nullable().optional(),
  actualDeliveryDate: z.string().nullable().optional(),
  shippingCost: z.number(),
  weight: z.number().optional(),
  dimensions: z.string().optional(),
  status: shipmentStatusEnum,
  notes: z.string().optional(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional(),
  version: z.number().optional(),
})

export type Shipment = z.infer<typeof shipmentSchema>

// Paginated shipment list
export const shipmentListResponseSchema = z.object({
  content: z.array(shipmentSchema),
  totalElements: z.number(),
  totalPages: z.number(),
  size: z.number(),
  number: z.number(),
})

export type ShipmentListResponse = z.infer<typeof shipmentListResponseSchema>
