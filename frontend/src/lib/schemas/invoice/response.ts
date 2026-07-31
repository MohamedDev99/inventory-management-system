import { z } from "zod"

// ========================================
// INVOICE RESPONSE SCHEMAS
// ========================================

// Invoice status enum
export const invoiceStatusEnum = z.enum(["DRAFT", "SENT", "PAID", "OVERDUE", "CANCELLED"])

export type InvoiceStatus = z.infer<typeof invoiceStatusEnum>

// Sales order reference in invoice
export const invoiceSalesOrderSchema = z.object({
  id: z.number(),
  soNumber: z.string(),
})

export type InvoiceSalesOrder = z.infer<typeof invoiceSalesOrderSchema>

// Customer reference in invoice
export const invoiceCustomerSchema = z.object({
  id: z.number(),
  customerCode: z.string(),
  contactName: z.string(),
})

export type InvoiceCustomer = z.infer<typeof invoiceCustomerSchema>

// Invoice item
export const invoiceItemSchema = z.object({
  id: z.number(),
  productId: z.number(),
  productName: z.string(),
  productSku: z.string(),
  quantity: z.number(),
  unitPrice: z.number(),
  taxAmount: z.number(),
  lineTotal: z.number(),
})

export type InvoiceItem = z.infer<typeof invoiceItemSchema>

// Invoice response
export const invoiceSchema = z.object({
  id: z.number(),
  invoiceNumber: z.string(),
  salesOrder: invoiceSalesOrderSchema.optional(),
  customer: invoiceCustomerSchema.optional(),
  invoiceDate: z.string(),
  dueDate: z.string().nullable().optional(),
  subtotal: z.number(),
  taxAmount: z.number(),
  discountAmount: z.number(),
  totalAmount: z.number(),
  amountPaid: z.number(),
  balanceDue: z.number(),
  status: invoiceStatusEnum,
  notes: z.string().optional(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional(),
  version: z.number().optional(),
})

export type Invoice = z.infer<typeof invoiceSchema>

// Paginated invoice list
export const invoiceListResponseSchema = z.object({
  content: z.array(invoiceSchema),
  totalElements: z.number(),
  totalPages: z.number(),
  size: z.number(),
  number: z.number(),
})

export type InvoiceListResponse = z.infer<typeof invoiceListResponseSchema>
