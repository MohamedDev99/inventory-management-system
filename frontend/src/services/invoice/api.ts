import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { invoiceSchema } from "@/lib/schemas/invoice"
import { createInvoiceRequestSchema } from "@/lib/schemas/invoice/request"
import type { PageParams } from "@/types"

export interface InvoiceParams extends PageParams {
  customerId?: number
  salesOrderId?: number
  invoiceStatus?: "DRAFT" | "SENT" | "PAID" | "OVERDUE" | "CANCELLED"
  startDate?: string
  endDate?: string
  overdue?: boolean
}

// Request types
export type CreateInvoiceRequest = Parameters<typeof createInvoiceRequestSchema.parse>[0]

export type UpdateInvoiceStatusRequest = {
  status: "DRAFT" | "SENT" | "PAID" | "OVERDUE" | "CANCELLED"
}

// Record payment request
export interface RecordInvoicePaymentRequest {
  paymentAmount: number
  paymentDate?: string
  paymentMethod?: "CASH" | "CARD" | "BANK_TRANSFER" | "CHECK"
  referenceNumber?: string
}

// GET /api/invoices - Paginated list
export async function getInvoices(params: InvoiceParams = {}) {
  const response = await api.get("/invoices", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(invoiceSchema)),
    response.data,
    { prefix: "Get Invoices Response" }
  )
  return validated
}

// GET /api/invoices/{id} - Full invoice detail
export async function getInvoice(id: number) {
  const response = await api.get(`/invoices/${id}`)
  const validated = validateResponse(
    apiResponseSchema(invoiceSchema),
    response.data,
    { prefix: "Get Invoice Response" }
  )
  return validated
}

// GET /api/invoices/overdue - Overdue invoices
export async function getOverdueInvoices() {
  const response = await api.get("/invoices/overdue")
  const validated = validateResponse(
    apiResponseSchema(z.array(invoiceSchema)),
    response.data,
    { prefix: "Get Overdue Invoices Response" }
  )
  return validated
}

// POST /api/invoices - Create new invoice
export async function createInvoice(data: Parameters<typeof createInvoiceRequestSchema.parse>[0]) {
  const validatedData = createInvoiceRequestSchema.parse(data)
  const response = await api.post("/invoices", validatedData)
  const validated = validateResponse(
    apiResponseSchema(invoiceSchema),
    response.data,
    { prefix: "Create Invoice Response" }
  )
  return validated
}

// PATCH /api/invoices/{id}/status - Update invoice status
export async function updateInvoiceStatus(id: number, invoiceStatus: "DRAFT" | "SENT" | "PAID" | "OVERDUE" | "CANCELLED") {
  const response = await api.patch(`/invoices/${id}/status`, { invoiceStatus })
  const validated = validateResponse(
    apiResponseSchema(invoiceSchema),
    response.data,
    { prefix: "Update Invoice Status Response" }
  )
  return validated
}

// POST /api/invoices/{id}/send - Send invoice
export async function sendInvoice(id: number) {
  const response = await api.post(`/invoices/${id}/send`)
  const validated = validateResponse(
    apiResponseSchema(invoiceSchema),
    response.data,
    { prefix: "Send Invoice Response" }
  )
  return validated
}

// GET /api/invoices/{id}/pdf - Download PDF
export async function getInvoicePdf(id: number): Promise<Blob> {
  const response = await api.get(`/invoices/${id}/pdf`, { responseType: 'blob' })
  return response.data
}

// POST /api/invoices/{id}/payment - Record payment
export async function recordInvoicePayment(id: number, data: {
  paymentAmount: number
  paymentDate?: string
  paymentMethod?: "CASH" | "CARD" | "BANK_TRANSFER" | "CHECK"
  referenceNumber?: string
}) {
  const response = await api.post(`/invoices/${id}/payment`, data)
  const validated = validateResponse(
    apiResponseSchema(invoiceSchema),
    response.data,
    { prefix: "Record Invoice Payment Response" }
  )
  return validated
}
