import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { salesOrderSchema, salesOrderSummarySchema } from "@/lib/schemas/sales-order"
import { invoiceSchema } from "@/lib/schemas/invoice"
import { createSalesOrderRequestSchema } from "@/lib/schemas/sales-order/request"
import type { PageParams } from "@/types"

export interface SalesOrderParams extends PageParams {
  customerId?: number
  warehouseId?: number
  status?: "PENDING" | "CONFIRMED" | "FULFILLED" | "SHIPPED" | "DELIVERED" | "CANCELLED"
  createdBy?: number
  startDate?: string
  endDate?: string
}

// Request types
export type CreateSalesOrderRequest = Parameters<typeof createSalesOrderRequestSchema.parse>[0]
export type UpdateSalesOrderRequest = CreateSalesOrderRequest

// GET /api/sales-orders - Paginated list
export async function getSalesOrders(params: SalesOrderParams = {}) {
  const response = await api.get("/sales-orders", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(salesOrderSummarySchema)),
    response.data,
    { prefix: "Get Sales Orders Response" }
  )
  return validated
}

// GET /api/sales-orders/{id} - Full SO with line items
export async function getSalesOrder(id: number) {
  const response = await api.get(`/sales-orders/${id}`)
  const validated = validateResponse(
    apiResponseSchema(salesOrderSchema),
    response.data,
    { prefix: "Get Sales Order Response" }
  )
  return validated
}

// POST /api/sales-orders - Create SO in PENDING status
export async function createSalesOrder(data: Parameters<typeof createSalesOrderRequestSchema.parse>[0], createdByUserId: number) {
  const validatedData = createSalesOrderRequestSchema.parse(data)
  const response = await api.post("/sales-orders", validatedData, { params: { createdByUserId } })
  const validated = validateResponse(
    apiResponseSchema(salesOrderSchema),
    response.data,
    { prefix: "Create Sales Order Response" }
  )
  return validated
}

// PUT /api/sales-orders/{id} - Update SO (PENDING status only)
export async function updateSalesOrder(id: number, data: Parameters<typeof createSalesOrderRequestSchema.parse>[0]) {
  const validatedData = createSalesOrderRequestSchema.parse(data)
  const response = await api.put(`/sales-orders/${id}`, validatedData)
  const validated = validateResponse(
    apiResponseSchema(salesOrderSchema),
    response.data,
    { prefix: "Update Sales Order Response" }
  )
  return validated
}

// DELETE /api/sales-orders/{id} - Permanently delete (PENDING status only)
export async function deleteSalesOrder(id: number): Promise<void> {
  await api.delete(`/sales-orders/${id}`)
}

// PATCH /api/sales-orders/{id}/confirm - PENDING → CONFIRMED
export async function confirmSalesOrder(id: number) {
  const response = await api.patch(`/sales-orders/${id}/confirm`)
  const validated = validateResponse(
    apiResponseSchema(salesOrderSchema),
    response.data,
    { prefix: "Confirm Sales Order Response" }
  )
  return validated
}

// PATCH /api/sales-orders/{id}/fulfill - CONFIRMED → FULFILLED
export async function fulfillSalesOrder(id: number, performedByUserId: number) {
  const response = await api.patch(`/sales-orders/${id}/fulfill`, {}, { params: { performedByUserId } })
  const validated = validateResponse(
    apiResponseSchema(salesOrderSchema),
    response.data,
    { prefix: "Fulfill Sales Order Response" }
  )
  return validated
}

// PATCH /api/sales-orders/{id}/ship - FULFILLED → SHIPPED
export async function shipSalesOrder(id: number, data?: { shippingDate?: string; carrier?: string; trackingNumber?: string }) {
  const response = await api.patch(`/sales-orders/${id}/ship`, data || {})
  const validated = validateResponse(
    apiResponseSchema(salesOrderSchema),
    response.data,
    { prefix: "Ship Sales Order Response" }
  )
  return validated
}

// PATCH /api/sales-orders/{id}/deliver - SHIPPED → DELIVERED
export async function deliverSalesOrder(id: number) {
  const response = await api.patch(`/sales-orders/${id}/deliver`)
  const validated = validateResponse(
    apiResponseSchema(salesOrderSchema),
    response.data,
    { prefix: "Deliver Sales Order Response" }
  )
  return validated
}

// PATCH /api/sales-orders/{id}/cancel - Cancel SO
export async function cancelSalesOrder(id: number, reason: string, performedByUserId: number) {
  const response = await api.patch(`/sales-orders/${id}/cancel`, { reason }, { params: { performedByUserId } })
  const validated = validateResponse(
    apiResponseSchema(salesOrderSchema),
    response.data,
    { prefix: "Cancel Sales Order Response" }
  )
  return validated
}

// GET /api/sales-orders/{id}/invoice - Get invoice
export async function getSalesOrderInvoice(id: number) {
  const response = await api.get(`/sales-orders/${id}/invoice`)
  const validated = validateResponse(
    apiResponseSchema(invoiceSchema),
    response.data,
    { prefix: "Get Sales Order Invoice Response" }
  )
  return validated
}

// POST /api/sales-orders/{id}/invoice - Create invoice from SO
export async function createSalesOrderInvoice(id: number) {
  const response = await api.post(`/sales-orders/${id}/invoice`)
  const validated = validateResponse(
    apiResponseSchema(invoiceSchema),
    response.data,
    { prefix: "Create Sales Order Invoice Response" }
  )
  return validated
}

// GET /api/sales-orders/{id}/pdf - Download PDF
export async function getSalesOrderPdf(id: number): Promise<Blob> {
  const response = await api.get(`/sales-orders/${id}/pdf`, { responseType: 'blob' })
  return response.data
}
