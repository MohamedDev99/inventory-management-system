// ========================================
// SHIPMENT TYPES
// ========================================

export type ShipmentStatus = 
  | "PENDING" 
  | "IN_TRANSIT" 
  | "DELIVERED" 
  | "DELAYED" 
  | "RTO" 
  | "RE_ATTEMPT"
  | "CANCELLED"

// Lighter sales order reference (as returned by API in shipment list)
export interface ShipmentSalesOrder {
  id: number
  soNumber: string
  customerName: string
}

// Lighter warehouse reference (as returned by API)
export interface ShipmentWarehouse {
  id: number
  name: string
  code: string
}

export interface Shipment {
  id: number
  shipmentNumber: string
  salesOrderId?: number
  salesOrder?: ShipmentSalesOrder
  shippedFromWarehouseId?: number
  shippedFromWarehouse?: ShipmentWarehouse
  carrier?: string
  trackingNumber?: string
  shippingCost?: number
  status: ShipmentStatus
  shippedDate?: string | null
  estimatedDeliveryDate?: string | null
  actualDeliveryDate?: string | null
  deliveredTo?: string
  deliveryAddress?: string
  receivedBy?: string
  deliveryNotes?: string
  version?: number
  createdAt: string
  updatedAt?: string
}
