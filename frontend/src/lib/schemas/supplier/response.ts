import { z } from "zod"

// ========================================
// SUPPLIER RESPONSE SCHEMAS
// ========================================

// Supplier response
export const supplierSchema = z.object({
  id: z.number(),
  name: z.string(),
  code: z.string(),
  contactPerson: z.string().optional(),
  email: z.string().email().optional(),
  phone: z.string().optional(),
  address: z.string().optional(),
  city: z.string().optional(),
  country: z.string().optional(),
  paymentTerms: z.string().optional(),
  rating: z.number().optional(),
  isActive: z.boolean(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional(),
  createdBy: z.string().optional(),
  updatedBy: z.string().optional(),
  version: z.number().optional(),
})

export type Supplier = z.infer<typeof supplierSchema>

// Supplier performance metrics
export const supplierMetricsSchema = z.object({
  totalOrders: z.number(),
  totalSpent: z.number(),
  averageOrderValue: z.number(),
  onTimeDeliveryRate: z.number(),
  averageDeliveryDays: z.number(),
  cancelledOrders: z.number(),
  cancelledOrderRate: z.number(),
  qualityIssues: z.number(),
  qualityIssueRate: z.number(),
  responseTime: z.string(),
  pendingOrders: z.number(),
  completedOrders: z.number(),
  completionRate: z.number(),
})

export type SupplierMetrics = z.infer<typeof supplierMetricsSchema>

// Supplier trends
export const supplierTrendsSchema = z.object({
  orderFrequency: z.string(),
  spendTrend: z.string(),
  qualityTrend: z.string(),
  deliveryTrend: z.string(),
})

export type SupplierTrends = z.infer<typeof supplierTrendsSchema>

// Supplier performance response
export const supplierPerformanceSchema = z.object({
  supplierId: z.number(),
  supplierName: z.string(),
  supplierCode: z.string(),
  rating: z.number(),
  metrics: supplierMetricsSchema,
  trends: supplierTrendsSchema,
  recommendations: z.string(),
})

export type SupplierPerformance = z.infer<typeof supplierPerformanceSchema>

// Paginated supplier list
export const supplierListResponseSchema = z.object({
  content: z.array(supplierSchema),
  totalElements: z.number(),
  totalPages: z.number(),
  size: z.number(),
  number: z.number(),
})

export type SupplierListResponse = z.infer<typeof supplierListResponseSchema>
