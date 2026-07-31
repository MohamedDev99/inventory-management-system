import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { shipmentSchema } from "@/lib/schemas/shipment"
import { createShipmentRequestSchema } from "@/lib/schemas/shipment/request"
import type { PageParams } from "@/types"

export interface ShipmentsParams extends PageParams {
  warehouseId?: number
  status?: "PENDING" | "IN_TRANSIT" | "DELIVERED" | "CANCELLED"
  carrier?: string
}

// Alias for backward compatibility
export type ShipmentParams = ShipmentsParams

// Request types
export type CreateShipmentRequest = Parameters<typeof createShipmentRequestSchema.parse>[0]

// GET /api/shipments - Paginated list
export async function getShipments(params: ShipmentsParams = {}) {
  const response = await api.get("/shipments", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(shipmentSchema)),
    response.data,
    { prefix: "Get Shipments Response" }
  )
  return validated
}

// GET /api/shipments/{id} - Full shipment detail
export async function getShipment(id: number) {
  const response = await api.get(`/shipments/${id}`)
  const validated = validateResponse(
    apiResponseSchema(shipmentSchema),
    response.data,
    { prefix: "Get Shipment Response" }
  )
  return validated
}

// GET /api/shipments/tracking/{trackingNumber} - Lookup by tracking number
export async function getShipmentByTracking(trackingNumber: string) {
  const response = await api.get(`/shipments/tracking/${trackingNumber}`)
  const validated = validateResponse(
    apiResponseSchema(shipmentSchema),
    response.data,
    { prefix: "Get Shipment by Tracking Response" }
  )
  return validated
}

// GET /api/shipments/sales-order/{salesOrderId} - Shipments for a sales order
export async function getShipmentsBySalesOrder(salesOrderId: number) {
  const response = await api.get(`/shipments/sales-order/${salesOrderId}`)
  const validated = validateResponse(
    apiResponseSchema(z.array(shipmentSchema)),
    response.data,
    { prefix: "Get Shipments by Sales Order Response" }
  )
  return validated
}

// GET /api/shipments/pending - PENDING or IN_TRANSIT shipments
export async function getPendingShipments() {
  const response = await api.get("/shipments/pending")
  const validated = validateResponse(
    apiResponseSchema(z.array(shipmentSchema)),
    response.data,
    { prefix: "Get Pending Shipments Response" }
  )
  return validated
}

// GET /api/shipments/overdue - Past estimated delivery, not delivered
export async function getOverdueShipments() {
  const response = await api.get("/shipments/overdue")
  const validated = validateResponse(
    apiResponseSchema(z.array(shipmentSchema)),
    response.data,
    { prefix: "Get Overdue Shipments Response" }
  )
  return validated
}

// POST /api/shipments - Create new shipment
export async function createShipment(data: Parameters<typeof createShipmentRequestSchema.parse>[0]) {
  const validatedData = createShipmentRequestSchema.parse(data)
  const response = await api.post("/shipments", validatedData)
  const validated = validateResponse(
    apiResponseSchema(shipmentSchema),
    response.data,
    { prefix: "Create Shipment Response" }
  )
  return validated
}

// PATCH /api/shipments/{id}/status - Update status
export async function updateShipmentStatus(id: number, data: { status: "PENDING" | "IN_TRANSIT" | "DELIVERED" | "CANCELLED"; location?: string }) {
  const response = await api.patch(`/shipments/${id}/status`, data)
  const validated = validateResponse(
    apiResponseSchema(shipmentSchema),
    response.data,
    { prefix: "Update Shipment Status Response" }
  )
  return validated
}

// PATCH /api/shipments/{id}/deliver - Mark as delivered
export async function deliverShipment(id: number, data?: { actualDeliveryDate?: string; receivedBy?: string; notes?: string }) {
  const response = await api.patch(`/shipments/${id}/deliver`, data || {})
  const validated = validateResponse(
    apiResponseSchema(shipmentSchema),
    response.data,
    { prefix: "Deliver Shipment Response" }
  )
  return validated
}

// GET /api/shipments/{id}/track - Get tracking info
export async function getShipmentTracking(id: number) {
  const response = await api.get(`/shipments/${id}/track`)
  // Return raw response for complex nested objects
  return response.data
}
