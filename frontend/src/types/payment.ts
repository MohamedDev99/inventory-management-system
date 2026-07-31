// ========================================
// PAYMENT TYPES
// ========================================

import type { SalesOrderRef, SalesOrder } from "./order"
import type { CustomerRef, Customer } from "./customer"

export interface Payment {
  id: number
  paymentNumber: string
  // Use SalesOrderRef for list views (light reference), SalesOrder for detail views
  salesOrder?: SalesOrderRef | SalesOrder
  // Use CustomerRef for list views (light reference), Customer for detail views
  customer?: CustomerRef | Customer
  amount: number
  paymentMethod: "CASH" | "CARD" | "BANK_TRANSFER" | "CHECK"
  // NOTE: paymentStatus is available in detail view, status is used in list views
  paymentStatus?: "PENDING" | "COMPLETED" | "FAILED" | "REFUNDED"
  status?: "PENDING" | "COMPLETED" | "FAILED" | "REFUNDED"
  paymentDate: string
  notes?: string
  createdAt: string
  updatedAt?: string
  version?: number
}

export interface PaymentFormData {
  salesOrderId?: number
  customerId?: number
  amount: number
  paymentMethod: "CASH" | "CARD" | "BANK_TRANSFER" | "CHECK"
  paymentDate: string
  notes?: string
}
