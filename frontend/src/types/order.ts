// ========================================
// ORDER TYPES
// ========================================

// Light reference types (as returned by API in list views)
export interface SalesOrderRef {
  // NOTE: This is a partial/reference type used in list views
  // Full SalesOrder type is defined below with all details
  id: number
  soNumber: string
}

export interface PurchaseOrderSupplier {
  id: number
  name: string
  code: string
  contactPerson?: string
  email?: string
}

export interface PurchaseOrderWarehouse {
  id: number
  name: string
  code: string
}

export interface PurchaseOrderUser {
  id: number
  username: string
  email?: string
}

export interface PurchaseOrderItem {
  id: number
  productId: number
  productSku: string
  productName: string
  quantityOrdered: number
  quantityReceived: number
  unitPrice: number
  lineTotal: number
}

export type PurchaseOrderStatus = 
  | "DRAFT" 
  | "SUBMITTED" 
  | "APPROVED" 
  | "RECEIVED" 
  | "REJECTED" 
  | "CANCELLED"

// Purchase order as returned by API (matches schema)
export interface PurchaseOrder {
  id: number
  poNumber: string
  supplierId: number
  supplierName: string
  supplierCode: string
  warehouseId: number
  warehouseName: string
  status: PurchaseOrderStatus
  orderDate: string
  expectedDeliveryDate?: string | null
  actualDeliveryDate?: string | null
  itemCount: number
  subtotal: number
  taxAmount: number
  discountAmount: number
  totalAmount: number
  // Full details (only in single PO view)
  supplier?: PurchaseOrderSupplier
  warehouse?: PurchaseOrderWarehouse
  createdByUser?: PurchaseOrderUser
  items?: PurchaseOrderItem[]
  notes?: string
  version?: number
  createdAt: string
  updatedAt?: string
}

// Sales order reference types
export interface SalesOrderCustomer {
  id: number
  customerCode: string
  companyName?: string
  contactName: string
}

export interface SalesOrderWarehouse {
  id: number
  name: string
  code: string
}

export interface SalesOrderUser {
  id: number
  username: string
  email?: string
}

export interface SalesOrderItem {
  id: number
  productId: number
  productSku: string
  productName: string
  quantityOrdered: number
  quantityShipped: number
  unitPrice: number
  lineTotal: number
}

export type SalesOrderStatus = 
  | "PENDING" 
  | "CONFIRMED" 
  | "PROCESSING"
  | "FULFILLED"
  | "SHIPPED" 
  | "DELIVERED" 
  | "CANCELLED"

export interface SalesOrder {
  id: number
  soNumber: string
  customerId: number
  customerName: string
  customerCode: string
  warehouseId: number
  warehouseName: string
  status: SalesOrderStatus
  orderDate: string
  fulfillmentDate?: string | null
  shippingDate?: string | null
  deliveryDate?: string | null
  itemCount: number
  subtotal: number
  taxAmount: number
  shippingCost?: number
  discountAmount?: number
  totalAmount: number
  // Full details
  customer?: SalesOrderCustomer
  warehouse?: SalesOrderWarehouse
  createdByUser?: SalesOrderUser
  items?: SalesOrderItem[]
  notes?: string
  createdAt: string
  updatedAt?: string
}

export interface OrderStatusHistory {
  status: string
  timestamp: string
  user: string
}
