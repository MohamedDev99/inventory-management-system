import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { purchaseOrderSchema, purchaseOrderSummarySchema } from "@/lib/schemas/purchase-order"
import { createPurchaseOrderRequestSchema } from "@/lib/schemas/purchase-order/request"
import type { PageParams } from "@/types"

export interface PurchaseOrderParams extends PageParams {
  supplierId?: number
  warehouseId?: number
  status?: "DRAFT" | "SUBMITTED" | "APPROVED" | "RECEIVED" | "CANCELLED"
  createdBy?: number
  startDate?: string
  endDate?: string
}

// Request types
export type CreatePurchaseOrderRequest = Parameters<typeof createPurchaseOrderRequestSchema.parse>[0]
export type UpdatePurchaseOrderRequest = CreatePurchaseOrderRequest

export type ReceivePurchaseOrderRequest = {
  actualDeliveryDate?: string
  notes?: string
  items: { itemId: number; quantityReceived: number }[]
}

// GET /api/purchase-orders - Paginated list
export async function getPurchaseOrders(params: PurchaseOrderParams = {}) {
  const response = await api.get("/purchase-orders", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(purchaseOrderSummarySchema)),
    response.data,
    { prefix: "Get Purchase Orders Response" }
  )
  return validated
}

// GET /api/purchase-orders/{id} - Full PO detail with line items
export async function getPurchaseOrder(id: number) {
  const response = await api.get(`/purchase-orders/${id}`)
  const validated = validateResponse(
    apiResponseSchema(purchaseOrderSchema),
    response.data,
    { prefix: "Get Purchase Order Response" }
  )
  return validated
}

// GET /api/purchase-orders/pending-approval - POs in SUBMITTED status
export async function getPendingApprovalPurchaseOrders(params: PageParams = {}) {
  const response = await api.get("/purchase-orders/pending-approval", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(purchaseOrderSummarySchema)),
    response.data,
    { prefix: "Get Pending Approval Purchase Orders Response" }
  )
  return validated
}

// POST /api/purchase-orders - Create PO in DRAFT status
export async function createPurchaseOrder(data: Parameters<typeof createPurchaseOrderRequestSchema.parse>[0], createdByUserId: number) {
  const validatedData = createPurchaseOrderRequestSchema.parse(data)
  const response = await api.post("/purchase-orders", validatedData, { params: { createdByUserId } })
  const validated = validateResponse(
    apiResponseSchema(purchaseOrderSchema),
    response.data,
    { prefix: "Create Purchase Order Response" }
  )
  return validated
}

// PUT /api/purchase-orders/{id} - Update PO (DRAFT status only)
export async function updatePurchaseOrder(id: number, data: Parameters<typeof createPurchaseOrderRequestSchema.parse>[0]) {
  const validatedData = createPurchaseOrderRequestSchema.parse(data)
  const response = await api.put(`/purchase-orders/${id}`, validatedData)
  const validated = validateResponse(
    apiResponseSchema(purchaseOrderSchema),
    response.data,
    { prefix: "Update Purchase Order Response" }
  )
  return validated
}

// DELETE /api/purchase-orders/{id} - Permanently delete (DRAFT status only)
export async function deletePurchaseOrder(id: number): Promise<void> {
  await api.delete(`/purchase-orders/${id}`)
}

// PATCH /api/purchase-orders/{id}/submit - DRAFT → SUBMITTED
export async function submitPurchaseOrder(id: number) {
  const response = await api.patch(`/purchase-orders/${id}/submit`)
  const validated = validateResponse(
    apiResponseSchema(purchaseOrderSchema),
    response.data,
    { prefix: "Submit Purchase Order Response" }
  )
  return validated
}

// PATCH /api/purchase-orders/{id}/approve - SUBMITTED → APPROVED
export async function approvePurchaseOrder(id: number) {
  const response = await api.patch(`/purchase-orders/${id}/approve`)
  const validated = validateResponse(
    apiResponseSchema(purchaseOrderSchema),
    response.data,
    { prefix: "Approve Purchase Order Response" }
  )
  return validated
}

// PATCH /api/purchase-orders/{id}/reject - SUBMITTED → DRAFT
export async function rejectPurchaseOrder(id: number, reason: string) {
  const response = await api.patch(`/purchase-orders/${id}/reject`, { reason })
  const validated = validateResponse(
    apiResponseSchema(purchaseOrderSchema),
    response.data,
    { prefix: "Reject Purchase Order Response" }
  )
  return validated
}

// PATCH /api/purchase-orders/{id}/cancel - Cancel PO
export async function cancelPurchaseOrder(id: number, reason: string) {
  const response = await api.patch(`/purchase-orders/${id}/cancel`, { reason })
  const validated = validateResponse(
    apiResponseSchema(purchaseOrderSchema),
    response.data,
    { prefix: "Cancel Purchase Order Response" }
  )
  return validated
}

// POST /api/purchase-orders/{id}/receive - Mark APPROVED PO as RECEIVED
export async function receivePurchaseOrder(id: number, data: {
  actualDeliveryDate?: string
  notes?: string
  items: { itemId: number; quantityReceived: number }[]
}, performedByUserId: number) {
  const response = await api.post(`/purchase-orders/${id}/receive`, data, { params: { performedByUserId } })
  const validated = validateResponse(
    apiResponseSchema(purchaseOrderSchema),
    response.data,
    { prefix: "Receive Purchase Order Response" }
  )
  return validated
}

// GET /api/purchase-orders/{id}/pdf - Download PDF
export async function getPurchaseOrderPdf(id: number): Promise<Blob> {
  const response = await api.get(`/purchase-orders/${id}/pdf`, { responseType: 'blob' })
  return response.data
}
