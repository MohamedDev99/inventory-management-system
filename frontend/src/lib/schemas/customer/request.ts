import { z } from "zod"

// ========================================
// CUSTOMER REQUEST SCHEMAS
// ========================================

// Customer type enum
export const customerTypeEnum = z.enum(["RETAIL", "WHOLESALE", "DISTRIBUTOR", "CORPORATE"])

// Create customer request
export const createCustomerRequestSchema = z.object({
  customerCode: z
    .string()
    .min(1, "Customer code is required")
    .regex(/^[A-Z0-9\-]+$/, "Customer code must contain only uppercase letters, digits, and hyphens"),
  companyName: z.string().optional(),
  contactName: z.string().min(1, "Contact name is required"),
  email: z.string().email("Invalid email").optional().or(z.literal("")),
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
  customerType: customerTypeEnum.optional(),
  paymentTerms: z.string().optional(),
  creditLimit: z.number().min(0).optional(),
  taxId: z.string().optional(),
  isActive: z.boolean().optional(),
})

export type CreateCustomerRequest = z.infer<typeof createCustomerRequestSchema>

// Update customer request
export const updateCustomerRequestSchema = createCustomerRequestSchema.partial().refine(
  (data) => Object.keys(data).length > 0,
  { message: "At least one field is required" }
)

export type UpdateCustomerRequest = z.infer<typeof updateCustomerRequestSchema>

// Customer filter params
export const customerFilterParamsSchema = z.object({
  customerType: customerTypeEnum.optional(),
  isActive: z.boolean().optional(),
  city: z.string().optional(),
  country: z.string().optional(),
  search: z.string().optional(),
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
  sort: z.string().optional(),
})

export type CustomerFilterParams = z.infer<typeof customerFilterParamsSchema>
