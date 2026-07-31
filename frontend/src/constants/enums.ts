// ========================================
// MOEWARE IMS - ENUMS (converted to const objects for erasableSyntaxOnly compatibility)
// ========================================

// ========================================
// ORDER STATUS ENUMS
// ========================================

export const PurchaseOrderStatus = {
  DRAFT: "DRAFT",
  SUBMITTED: "SUBMITTED",
  APPROVED: "APPROVED",
  RECEIVED: "RECEIVED",
  REJECTED: "REJECTED",
  CANCELLED: "CANCELLED",
} as const

export type PurchaseOrderStatus = typeof PurchaseOrderStatus[keyof typeof PurchaseOrderStatus]

export const SalesOrderStatus = {
  PENDING: "PENDING",
  CONFIRMED: "CONFIRMED",
  PROCESSING: "PROCESSING",
  SHIPPED: "SHIPPED",
  DELIVERED: "DELIVERED",
  CANCELLED: "CANCELLED",
} as const

export type SalesOrderStatus = typeof SalesOrderStatus[keyof typeof SalesOrderStatus]

// ========================================
// SHIPMENT STATUS ENUMS
// ========================================

export const ShipmentStatus = {
  PENDING: "PENDING",
  IN_TRANSIT: "IN_TRANSIT",
  DELIVERED: "DELIVERED",
  DELAYED: "DELAYED",
  RTO: "RTO",
  RE_ATTEMPT: "RE_ATTEMPT",
} as const

export type ShipmentStatus = typeof ShipmentStatus[keyof typeof ShipmentStatus]

// ========================================
// PAYMENT STATUS ENUMS
// ========================================

export const PaymentStatus = {
  PENDING: "PENDING",
  COMPLETED: "COMPLETED",
  FAILED: "FAILED",
  REFUNDED: "REFUNDED",
} as const

export type PaymentStatus = typeof PaymentStatus[keyof typeof PaymentStatus]

export const PaymentMethod = {
  CASH: "CASH",
  CARD: "CARD",
  BANK_TRANSFER: "BANK_TRANSFER",
  CHECK: "CHECK",
} as const

export type PaymentMethod = typeof PaymentMethod[keyof typeof PaymentMethod]

// ========================================
// INVOICE STATUS ENUMS
// ========================================

export const InvoiceStatus = {
  DRAFT: "DRAFT",
  SENT: "SENT",
  PAID: "PAID",
  OVERDUE: "OVERDUE",
  CANCELLED: "CANCELLED",
} as const

export type InvoiceStatus = typeof InvoiceStatus[keyof typeof InvoiceStatus]

// ========================================
// REPORT TYPES ENUMS
// ========================================

export const ReportType = {
  STOCK_VALUATION: "STOCK_VALUATION",
  INVENTORY_MOVEMENT: "INVENTORY_MOVEMENT",
  SALES_ANALYSIS: "SALES_ANALYSIS",
  PURCHASE_HISTORY: "PURCHASE_HISTORY",
  LOW_STOCK_ALERT: "LOW_STOCK_ALERT",
} as const

export type ReportType = typeof ReportType[keyof typeof ReportType]

export const ReportFormat = {
  PDF: "PDF",
  EXCEL: "EXCEL",
  CSV: "CSV",
} as const

export type ReportFormat = typeof ReportFormat[keyof typeof ReportFormat]

export const ReportStatus = {
  PENDING: "PENDING",
  COMPLETED: "COMPLETED",
  FAILED: "FAILED",
} as const

export type ReportStatus = typeof ReportStatus[keyof typeof ReportStatus]

// ========================================
// NOTIFICATION ENUMS
// ========================================

export const NotificationType = {
  LOW_STOCK: "LOW_STOCK",
  ORDER_APPROVED: "ORDER_APPROVED",
  ORDER_RECEIVED: "ORDER_RECEIVED",
  SHIPMENT: "SHIPMENT",
  STOCK_ADJUSTMENT: "STOCK_ADJUSTMENT",
  SYSTEM: "SYSTEM",
} as const

export type NotificationType = typeof NotificationType[keyof typeof NotificationType]

export const NotificationPriority = {
  LOW: "LOW",
  MEDIUM: "MEDIUM",
  HIGH: "HIGH",
  CRITICAL: "CRITICAL",
} as const

export type NotificationPriority = typeof NotificationPriority[keyof typeof NotificationPriority]

// ========================================
// CUSTOMER TYPES ENUMS
// ========================================

export const CustomerType = {
  RETAIL: "RETAIL",
  WHOLESALE: "WHOLESALE",
  DISTRIBUTOR: "DISTRIBUTOR",
  CORPORATE: "CORPORATE",
} as const

export type CustomerType = typeof CustomerType[keyof typeof CustomerType]

// ========================================
// STOCK STATUS ENUMS
// ========================================

export const StockStatus = {
  IN_STOCK: "IN_STOCK",
  LOW_STOCK: "LOW_STOCK",
  OUT_OF_STOCK: "OUT_OF_STOCK",
} as const

export type StockStatus = typeof StockStatus[keyof typeof StockStatus]

// ========================================
// USER ROLES ENUMS
// ========================================

export const UserRole = {
  ADMIN: "ADMIN",
  MANAGER: "MANAGER",
  STAFF: "STAFF",
  VIEWER: "VIEWER",
} as const

export type UserRole = typeof UserRole[keyof typeof UserRole]

// ========================================
// TIME PERIODS ENUMS
// ========================================

export const TimePeriod = {
  DAY: "DAY",
  WEEK: "WEEK",
  MONTH: "MONTH",
  QUARTER: "QUARTER",
  YEAR: "YEAR",
} as const

export type TimePeriod = typeof TimePeriod[keyof typeof TimePeriod]

// ========================================
// SCHEDULE FREQUENCY ENUMS
// ========================================

export const ScheduleFrequency = {
  DAILY: "DAILY",
  WEEKLY: "WEEKLY",
  MONTHLY: "MONTHLY",
} as const

export type ScheduleFrequency = typeof ScheduleFrequency[keyof typeof ScheduleFrequency]
