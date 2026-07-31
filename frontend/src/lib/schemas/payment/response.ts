import { z } from "zod"

// ========================================
// PAYMENT RESPONSE SCHEMAS
// ========================================

// Payment status enum
export const paymentStatusEnum = z.enum(["PENDING", "COMPLETED", "FAILED", "REFUNDED"])

export type PaymentStatus = z.infer<typeof paymentStatusEnum>

// Sales order reference in payment
export const paymentSalesOrderSchema = z.object({
  id: z.number(),
  soNumber: z.string(),
})

export type PaymentSalesOrder = z.infer<typeof paymentSalesOrderSchema>

// Customer reference in payment
export const paymentCustomerSchema = z.object({
  id: z.number(),
  customerCode: z.string(),
  contactName: z.string(),
})

export type PaymentCustomer = z.infer<typeof paymentCustomerSchema>

// Payment response
export const paymentSchema = z.object({
  id: z.number(),
  paymentNumber: z.string(),
  salesOrder: paymentSalesOrderSchema.optional(),
  customer: paymentCustomerSchema.optional(),
  amount: z.number(),
  paymentMethod: z.enum(["CASH", "CARD", "BANK_TRANSFER", "CHECK"]),
  paymentDate: z.string(),
  status: paymentStatusEnum,
  notes: z.string().optional(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional(),
  version: z.number().optional(),
})

export type Payment = z.infer<typeof paymentSchema>

// Paginated payment list
export const paymentListResponseSchema = z.object({
  content: z.array(paymentSchema),
  totalElements: z.number(),
  totalPages: z.number(),
  size: z.number(),
  number: z.number(),
})

export type PaymentListResponse = z.infer<typeof paymentListResponseSchema>
