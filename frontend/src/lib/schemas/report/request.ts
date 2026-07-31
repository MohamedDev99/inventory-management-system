import { z } from "zod"

// ========================================
// REPORT REQUEST SCHEMAS
// ========================================

// Report generation request
export const reportGenerationRequestSchema = z.object({
  name: z.string().min(1, "Report name is required"),
  type: z.enum([
    "STOCK_VALUATION",
    "INVENTORY_MOVEMENT",
    "SALES_ANALYSIS",
    "PURCHASE_HISTORY",
    "LOW_STOCK_ALERT",
    "SUPPLIER_PERFORMANCE",
    "CUSTOMER_STATEMENT",
  ]),
  format: z.enum(["PDF", "EXCEL", "CSV"]),
  startDate: z.string().optional(),
  endDate: z.string().optional(),
  // Additional filters
  warehouseId: z.number().optional(),
  categoryId: z.number().optional(),
  supplierId: z.number().optional(),
  customerId: z.number().optional(),
})

export type ReportGenerationRequest = z.infer<typeof reportGenerationRequestSchema>
