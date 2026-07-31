import { z } from "zod"
import { customerTypeEnum } from "./request"

// ========================================
// CUSTOMER RESPONSE SCHEMAS
// ========================================

// Customer response
export const customerSchema = z.object({
  id: z.number(),
  customerCode: z.string(),
  companyName: z.string().optional(),
  contactName: z.string(),
  email: z.string().email().optional(),
  phone: z.string().optional(),
  mobile: z.string().optional(),
  billingAddress: z.string().optional(),
  billingCity: z.string().optional(),
  billingState: z.string().optional(),
  billingCountry: z.string().optional(),
  billingPostalCode: z.string().optional(),
  shippingAddress: z.string().optional(),
  shippingCity: z.string().optional(),
  shippingState: z.string().optional(),
  shippingCountry: z.string().optional(),
  shippingPostalCode: z.string().optional(),
  creditLimit: z.number().optional(),
  paymentTerms: z.string().optional(),
  customerType: customerTypeEnum.optional(),
  taxId: z.string().optional(),
  isActive: z.boolean(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional(),
  version: z.number().optional(),
})

export type Customer = z.infer<typeof customerSchema>

// Customer transaction
export const customerTransactionSchema = z.object({
  date: z.string(),
  type: z.enum(["INVOICE", "PAYMENT", "CREDIT_NOTE", "DEBIT_NOTE"]),
  referenceNumber: z.string(),
  description: z.string(),
  debit: z.number(),
  credit: z.number(),
  balance: z.number(),
})

export type CustomerTransaction = z.infer<typeof customerTransactionSchema>

// Customer statement period
export const customerStatementPeriodSchema = z.object({
  startDate: z.string(),
  endDate: z.string(),
})

export type CustomerStatementPeriod = z.infer<typeof customerStatementPeriodSchema>

// Customer statement response
export const customerStatementSchema = z.object({
  customerId: z.number(),
  customerName: z.string(),
  statementPeriod: customerStatementPeriodSchema,
  openingBalance: z.number(),
  transactions: z.array(customerTransactionSchema),
  closingBalance: z.number(),
  totalInvoiced: z.number(),
  totalPaid: z.number(),
  creditLimit: z.number(),
  availableCredit: z.number(),
})

export type CustomerStatement = z.infer<typeof customerStatementSchema>

// Paginated customer list
export const customerListResponseSchema = z.object({
  content: z.array(customerSchema),
  totalElements: z.number(),
  totalPages: z.number(),
  size: z.number(),
  number: z.number(),
})

export type CustomerListResponse = z.infer<typeof customerListResponseSchema>
