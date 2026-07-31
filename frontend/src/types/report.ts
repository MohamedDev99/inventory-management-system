// ========================================
// REPORT TYPES
// ========================================

export type ReportType = 
  | "STOCK_VALUATION" 
  | "INVENTORY_MOVEMENT" 
  | "SALES_ANALYSIS" 
  | "PURCHASE_HISTORY" 
  | "LOW_STOCK_ALERT"
  | "SUPPLIER_PERFORMANCE"
  | "CUSTOMER_STATEMENT"

// Lighter user reference for reports
export interface ReportUser {
  id: number
  username: string
  email?: string
}

// Report as returned by API (matches schema)
export interface Report {
  id: number
  name: string
  type: ReportType
  format: "PDF" | "EXCEL" | "CSV"
  status: "PENDING" | "PROCESSING" | "COMPLETED" | "FAILED"
  fileSize?: string
  fileUrl?: string
  generatedBy?: ReportUser
  generatedAt?: string
  expiresAt?: string
  downloadUrl?: string
}

export interface ReportGenerationRequest {
  name: string
  type: ReportType
  format: "PDF" | "EXCEL" | "CSV"
  startDate?: string
  endDate?: string
  parameters?: Record<string, unknown>
}
