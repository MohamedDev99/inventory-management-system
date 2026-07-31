// ========================================
// MOEWARE IMS - STATUS MAPS
// ========================================

import {
  PurchaseOrderStatus,
  SalesOrderStatus,
  ShipmentStatus,
  PaymentStatus,
  InvoiceStatus,
  ReportStatus,
  NotificationType,
  NotificationPriority,
  CustomerType,
  StockStatus,
  ReportFormat,
} from "./enums"

// ========================================
// PURCHASE ORDER STATUS MAPS
// ========================================

export const purchaseOrderStatusLabels: Record<PurchaseOrderStatus, string> = {
  [PurchaseOrderStatus.DRAFT]: "Drafted",
  [PurchaseOrderStatus.SUBMITTED]: "Pending",
  [PurchaseOrderStatus.APPROVED]: "Approved",
  [PurchaseOrderStatus.RECEIVED]: "Received",
  [PurchaseOrderStatus.REJECTED]: "Rejected",
  [PurchaseOrderStatus.CANCELLED]: "Cancelled",
}

export const purchaseOrderStatusColors: Record<PurchaseOrderStatus, string> = {
  [PurchaseOrderStatus.DRAFT]: "bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-300",
  [PurchaseOrderStatus.SUBMITTED]: "bg-yellow-100 text-yellow-700 dark:bg-yellow-900 dark:text-yellow-300",
  [PurchaseOrderStatus.APPROVED]: "bg-blue-100 text-blue-700 dark:bg-blue-900 dark:text-blue-300",
  [PurchaseOrderStatus.RECEIVED]: "bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300",
  [PurchaseOrderStatus.REJECTED]: "bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300",
  [PurchaseOrderStatus.CANCELLED]: "bg-gray-100 text-gray-500 dark:bg-gray-800 dark:text-gray-400",
}

// ========================================
// SALES ORDER STATUS MAPS
// ========================================

export const salesOrderStatusLabels: Record<SalesOrderStatus, string> = {
  [SalesOrderStatus.PENDING]: "Pending",
  [SalesOrderStatus.CONFIRMED]: "Confirmed",
  [SalesOrderStatus.PROCESSING]: "Processing",
  [SalesOrderStatus.SHIPPED]: "In Transit",
  [SalesOrderStatus.DELIVERED]: "Delivered",
  [SalesOrderStatus.CANCELLED]: "Cancelled",
}

export const salesOrderStatusColors: Record<SalesOrderStatus, string> = {
  [SalesOrderStatus.PENDING]: "bg-yellow-100 text-yellow-700 dark:bg-yellow-900 dark:text-yellow-300",
  [SalesOrderStatus.CONFIRMED]: "bg-blue-100 text-blue-700 dark:bg-blue-900 dark:text-blue-300",
  [SalesOrderStatus.PROCESSING]: "bg-purple-100 text-purple-700 dark:bg-purple-900 dark:text-purple-300",
  [SalesOrderStatus.SHIPPED]: "bg-orange-100 text-orange-700 dark:bg-orange-900 dark:text-orange-300",
  [SalesOrderStatus.DELIVERED]: "bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300",
  [SalesOrderStatus.CANCELLED]: "bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300",
}

// ========================================
// SHIPMENT STATUS MAPS
// ========================================

export const shipmentStatusLabels: Record<ShipmentStatus, string> = {
  [ShipmentStatus.PENDING]: "Pending",
  [ShipmentStatus.IN_TRANSIT]: "In Transit",
  [ShipmentStatus.DELIVERED]: "Delivered",
  [ShipmentStatus.DELAYED]: "Delayed",
  [ShipmentStatus.RTO]: "RTO",
  [ShipmentStatus.RE_ATTEMPT]: "Re-attempt",
}

export const shipmentStatusColors: Record<ShipmentStatus, string> = {
  [ShipmentStatus.PENDING]: "bg-yellow-100 text-yellow-700 dark:bg-yellow-900 dark:text-yellow-300",
  [ShipmentStatus.IN_TRANSIT]: "bg-blue-100 text-blue-700 dark:bg-blue-900 dark:text-blue-300",
  [ShipmentStatus.DELIVERED]: "bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300",
  [ShipmentStatus.DELAYED]: "bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300",
  [ShipmentStatus.RTO]: "bg-orange-100 text-orange-700 dark:bg-orange-900 dark:text-orange-300",
  [ShipmentStatus.RE_ATTEMPT]: "bg-purple-100 text-purple-700 dark:bg-purple-900 dark:text-purple-300",
}

// ========================================
// PAYMENT STATUS MAPS
// ========================================

export const paymentStatusLabels: Record<PaymentStatus, string> = {
  [PaymentStatus.PENDING]: "Pending",
  [PaymentStatus.COMPLETED]: "Completed",
  [PaymentStatus.FAILED]: "Failed",
  [PaymentStatus.REFUNDED]: "Refunded",
}

export const paymentStatusColors: Record<PaymentStatus, string> = {
  [PaymentStatus.PENDING]: "bg-yellow-100 text-yellow-700 dark:bg-yellow-900 dark:text-yellow-300",
  [PaymentStatus.COMPLETED]: "bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300",
  [PaymentStatus.FAILED]: "bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300",
  [PaymentStatus.REFUNDED]: "bg-purple-100 text-purple-700 dark:bg-purple-900 dark:text-purple-300",
}

// ========================================
// INVOICE STATUS MAPS
// ========================================

export const invoiceStatusLabels: Record<InvoiceStatus, string> = {
  [InvoiceStatus.DRAFT]: "Draft",
  [InvoiceStatus.SENT]: "Sent",
  [InvoiceStatus.PAID]: "Paid",
  [InvoiceStatus.OVERDUE]: "Overdue",
  [InvoiceStatus.CANCELLED]: "Cancelled",
}

export const invoiceStatusColors: Record<InvoiceStatus, string> = {
  [InvoiceStatus.DRAFT]: "bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-300",
  [InvoiceStatus.SENT]: "bg-blue-100 text-blue-700 dark:bg-blue-900 dark:text-blue-300",
  [InvoiceStatus.PAID]: "bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300",
  [InvoiceStatus.OVERDUE]: "bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300",
  [InvoiceStatus.CANCELLED]: "bg-gray-100 text-gray-500 dark:bg-gray-800 dark:text-gray-400",
}

// ========================================
// REPORT STATUS MAPS
// ========================================

export const reportStatusLabels: Record<ReportStatus, string> = {
  [ReportStatus.PENDING]: "Pending",
  [ReportStatus.COMPLETED]: "Completed",
  [ReportStatus.FAILED]: "Failed",
}

export const reportStatusColors: Record<ReportStatus, string> = {
  [ReportStatus.PENDING]: "bg-orange-100 text-orange-700 dark:bg-orange-900 dark:text-orange-300",
  [ReportStatus.COMPLETED]: "bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300",
  [ReportStatus.FAILED]: "bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300",
}

// ========================================
// REPORT FORMAT MAPS
// ========================================

export const reportFormatLabels: Record<ReportFormat, string> = {
  [ReportFormat.PDF]: "PDF",
  [ReportFormat.EXCEL]: "Excel",
  [ReportFormat.CSV]: "CSV",
}

export const reportFormatColors: Record<ReportFormat, string> = {
  [ReportFormat.PDF]: "bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300",
  [ReportFormat.EXCEL]: "bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300",
  [ReportFormat.CSV]: "bg-blue-100 text-blue-700 dark:bg-blue-900 dark:text-blue-300",
}

// ========================================
// NOTIFICATION TYPE MAPS
// ========================================

export const notificationTypeLabels: Record<NotificationType, string> = {
  [NotificationType.LOW_STOCK]: "Low Stock",
  [NotificationType.ORDER_APPROVED]: "Order Approved",
  [NotificationType.ORDER_RECEIVED]: "Order Received",
  [NotificationType.SHIPMENT]: "Shipment",
  [NotificationType.STOCK_ADJUSTMENT]: "Stock Adjustment",
  [NotificationType.SYSTEM]: "System",
}

// ========================================
// NOTIFICATION PRIORITY MAPS
// ========================================

export const notificationPriorityLabels: Record<NotificationPriority, string> = {
  [NotificationPriority.LOW]: "Low",
  [NotificationPriority.MEDIUM]: "Medium",
  [NotificationPriority.HIGH]: "High",
  [NotificationPriority.CRITICAL]: "Critical",
}

export const notificationPriorityColors: Record<NotificationPriority, string> = {
  [NotificationPriority.LOW]: "border-gray-400",
  [NotificationPriority.MEDIUM]: "border-blue-500",
  [NotificationPriority.HIGH]: "border-orange-500",
  [NotificationPriority.CRITICAL]: "border-red-500",
}

// ========================================
// CUSTOMER TYPE MAPS
// ========================================

export const customerTypeLabels: Record<CustomerType, string> = {
  [CustomerType.RETAIL]: "Retail",
  [CustomerType.WHOLESALE]: "Wholesale",
  [CustomerType.DISTRIBUTOR]: "Distributor",
  [CustomerType.CORPORATE]: "Corporate",
}

// ========================================
// STOCK STATUS MAPS
// ========================================

export const stockStatusLabels: Record<StockStatus, string> = {
  [StockStatus.IN_STOCK]: "In Stock",
  [StockStatus.LOW_STOCK]: "Low Stock",
  [StockStatus.OUT_OF_STOCK]: "Out of Stock",
}

export const stockStatusColors: Record<StockStatus, string> = {
  [StockStatus.IN_STOCK]: "bg-green-100 text-green-700 dark:bg-green-900 dark:text-green-300",
  [StockStatus.LOW_STOCK]: "bg-yellow-100 text-yellow-700 dark:bg-yellow-900 dark:text-yellow-300",
  [StockStatus.OUT_OF_STOCK]: "bg-red-100 text-red-700 dark:bg-red-900 dark:text-red-300",
}
