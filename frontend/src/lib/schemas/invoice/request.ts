import { z } from "zod"

// ========================================
// INVOICE REQUEST SCHEMAS
// ========================================

// Create invoice request
export const createInvoiceRequestSchema = z.object({
  salesOrderId: z.number().min(1, "Sales order is required"),
  invoiceDate: z.string().optional(),
  dueDate: z.string().optional(),
  notes: z.string().optional(),
})

export type CreateInvoiceRequest = z.infer<typeof createInvoiceRequestSchema>

// Update invoice status request
export const updateInvoiceStatusRequestSchema = z.object({
  status: z.enum(["SENT", "PAID", "OVERDUE", "CANCELLED"]),
})

export type UpdateInvoiceStatusRequest = z.infer<typeof updateInvoiceStatusRequestSchema>

// Invoice filter params
export const invoiceFilterParamsSchema = z.object({
  salesOrderId: z.number().optional(),
  customerId: z.number().optional(),
  invoiceStatus: z.enum(["DRAFT", "SENT", "PAID", "OVERDUE", "CANCELLED"]).optional(),
  startDate: z.string().optional(),
  endDate: z.string().optional(),
  overdue: z.boolean().optional(),
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
  sort: z.string().optional(),
})

export type InvoiceFilterParams = z.infer<typeof invoiceFilterParamsSchema>
