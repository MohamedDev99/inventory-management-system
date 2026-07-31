import { z } from "zod"

// ========================================
// REPORT RESPONSE SCHEMAS
// ========================================

// Report type enum
export const reportTypeEnum = z.enum([
  "STOCK_VALUATION",
  "INVENTORY_MOVEMENT",
  "SALES_ANALYSIS",
  "PURCHASE_HISTORY",
  "LOW_STOCK_ALERT",
  "SUPPLIER_PERFORMANCE",
  "CUSTOMER_STATEMENT",
])

export type ReportType = z.infer<typeof reportTypeEnum>

// Report format enum
export const reportFormatEnum = z.enum(["PDF", "EXCEL", "CSV"])

export type ReportFormat = z.infer<typeof reportFormatEnum>

// Report generation response
export const reportGenerationResponseSchema = z.object({
  id: z.number(),
  name: z.string(),
  type: reportTypeEnum,
  format: reportFormatEnum,
  status: z.enum(["PENDING", "PROCESSING", "COMPLETED", "FAILED"]),
  fileUrl: z.string().optional(),
  generatedAt: z.string().datetime().optional(),
  expiresAt: z.string().datetime().optional(),
})

export type ReportGenerationResponse = z.infer<typeof reportGenerationResponseSchema>
