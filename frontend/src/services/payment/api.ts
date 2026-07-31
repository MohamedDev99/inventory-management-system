import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { paymentSchema } from "@/lib/schemas/payment"
import { createPaymentRequestSchema } from "@/lib/schemas/payment/request"
import type { PageParams } from "@/types"

export interface PaymentParams extends PageParams {
  customerId?: number
  salesOrderId?: number
  paymentMethod?: "CASH" | "CARD" | "BANK_TRANSFER" | "CHECK"
  paymentStatus?: "PENDING" | "COMPLETED" | "FAILED" | "REFUNDED"
  startDate?: string
  endDate?: string
}

// Request types
export type CreatePaymentRequest = Parameters<typeof createPaymentRequestSchema.parse>[0]

// Update payment status request
export interface UpdatePaymentStatusRequest {
  status: "PENDING" | "COMPLETED" | "FAILED" | "REFUNDED"
  notes?: string
}

// Refund payment request
export interface RefundPaymentRequest {
  refundAmount: number
  reason?: string
  notes?: string
}

// GET /api/payments - Paginated list
export async function getPayments(params: PaymentParams = {}) {
  const response = await api.get("/payments", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(paymentSchema)),
    response.data,
    { prefix: "Get Payments Response" }
  )
  return validated
}

// GET /api/payments/{id} - Full payment detail
export async function getPayment(id: number) {
  const response = await api.get(`/payments/${id}`)
  const validated = validateResponse(
    apiResponseSchema(paymentSchema),
    response.data,
    { prefix: "Get Payment Response" }
  )
  return validated
}

// POST /api/payments - Create new payment
export async function createPayment(data: Parameters<typeof createPaymentRequestSchema.parse>[0]) {
  const validatedData = createPaymentRequestSchema.parse(data)
  const response = await api.post("/payments", validatedData)
  const validated = validateResponse(
    apiResponseSchema(paymentSchema),
    response.data,
    { prefix: "Create Payment Response" }
  )
  return validated
}

// PATCH /api/payments/{id}/status - Update payment status
export async function updatePaymentStatus(id: number, paymentStatus: "PENDING" | "COMPLETED" | "FAILED" | "REFUNDED", notes?: string) {
  const response = await api.patch(`/payments/${id}/status`, { paymentStatus, notes })
  const validated = validateResponse(
    apiResponseSchema(paymentSchema),
    response.data,
    { prefix: "Update Payment Status Response" }
  )
  return validated
}

// POST /api/payments/{id}/refund - Refund payment
export async function refundPayment(id: number, data: { refundAmount: number; reason?: string; notes?: string }) {
  const response = await api.post(`/payments/${id}/refund`, data)
  const validated = validateResponse(
    apiResponseSchema(paymentSchema),
    response.data,
    { prefix: "Refund Payment Response" }
  )
  return validated
}

// GET /api/payments/methods - Get available payment methods
export async function getPaymentMethods() {
  const response = await api.get("/payments/methods")
  const validated = validateResponse(
    apiResponseSchema(z.array(z.string())),
    response.data,
    { prefix: "Get Payment Methods Response" }
  )
  return validated
}
