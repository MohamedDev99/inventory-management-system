// ========================================
// INVOICE TYPES
// ========================================

import type { SalesOrderRef, SalesOrder } from "./order"
import type { CustomerRef, Customer } from "./customer"
import type { Product } from "./product"

export interface Invoice {
  id: number
  invoiceNumber: string
  // Use SalesOrderRef for list views (light reference), SalesOrder for detail views
  salesOrder?: SalesOrderRef | SalesOrder
  // Use CustomerRef for list views (light reference), Customer for detail views
  customer?: CustomerRef | Customer
  invoiceDate: string
  // NOTE: dueDate can be null from API, not just undefined
  dueDate?: string | null
  items?: InvoiceItem[]
  subtotal: number
  taxAmount: number
  // NOTE: discountAmount is returned from API but not defined in this interface
  discountAmount?: number
  totalAmount: number
  amountPaid: number
  balanceDue: number
  status: "DRAFT" | "SENT" | "PAID" | "OVERDUE" | "CANCELLED"
  createdAt: string
  updatedAt?: string
  version?: number
  // NOTE: customerId is available in API response but derived from customer relationship
  // customerId?: number
  notes?: string
}

export interface InvoiceItem {
  id: number
  product: Product
  quantity: number
  unitPrice: number
  lineTotal: number
}

export interface InvoiceFormData {
  salesOrderId: number
  invoiceDate: string
  dueDate?: string
  notes?: string
}
