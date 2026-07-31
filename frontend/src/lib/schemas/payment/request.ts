import { z } from "zod"

// ========================================
// PAYMENT REQUEST SCHEMAS
// ========================================

// Create payment request
export const createPaymentRequestSchema = z.object({
  salesOrderId: z.number().optional(),
  customerId: z.number().optional(),
  amount: z.number().min(0.01, "Amount must be positive"),
  paymentMethod: z.enum(["CASH", "CARD", "BANK_TRANSFER", "CHECK"]),
  paymentDate: z.string().optional(),
  notes: z.string().optional(),
})

export type CreatePaymentRequest = z.infer<typeof createPaymentRequestSchema>

// Payment filter params
export const paymentFilterParamsSchema = z.object({
  salesOrderId: z.number().optional(),
  customerId: z.number().optional(),
  paymentStatus: z.enum(["PENDING", "COMPLETED", "FAILED", "REFUNDED"]).optional(),
  paymentMethod: z.enum(["CASH", "CARD", "BANK_TRANSFER", "CHECK"]).optional(),
  startDate: z.string().optional(),
  endDate: z.string().optional(),
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
  sort: z.string().optional(),
})

export type PaymentFilterParams = z.infer<typeof paymentFilterParamsSchema>
