import { z } from "zod"

// ========================================
// SHIPMENT REQUEST SCHEMAS
// ========================================

// Create shipment request
export const createShipmentRequestSchema = z.object({
  salesOrderId: z.number().min(1, "Sales order is required"),
  warehouseId: z.number().min(1, "Warehouse is required"),
  shippedBy: z.number().optional(),
  carrier: z.string().min(1, "Carrier is required"),
  trackingNumber: z.string().optional(),
  shippingMethod: z.enum(["STANDARD", "EXPRESS", "OVERNIGHT"]).optional(),
  estimatedDeliveryDate: z.string().optional(),
  shippingCost: z.number().min(0).optional(),
  weight: z.number().min(0).optional(),
  dimensions: z.string().optional(),
  notes: z.string().optional(),
})

export type CreateShipmentRequest = z.infer<typeof createShipmentRequestSchema>

// Update shipment request
export const updateShipmentRequestSchema = createShipmentRequestSchema.partial().refine(
  (data) => Object.keys(data).length > 0,
  { message: "At least one field is required" }
)

export type UpdateShipmentRequest = z.infer<typeof updateShipmentRequestSchema>

// Shipment filter params
export const shipmentFilterParamsSchema = z.object({
  warehouseId: z.number().optional(),
  status: z.enum(["PENDING", "IN_TRANSIT", "DELIVERED", "CANCELLED"]).optional(),
  carrier: z.string().optional(),
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
  sort: z.string().optional(),
})

export type ShipmentFilterParams = z.infer<typeof shipmentFilterParamsSchema>
