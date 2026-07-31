// ========================================
// INVENTORY MOVEMENT TYPES
// ========================================

export interface InventoryMovement {
  id: number
  product: {
    id: number
    sku: string
    name: string
  }
  fromWarehouse?: {
    id: number
    name: string
  }
  toWarehouse?: {
    id: number
    name: string
  }
  quantity: number
  movementType: "TRANSFER" | "ADJUSTMENT" | "RECEIPT" | "SHIPMENT"
  reason?: string
  referenceNumber?: string
  performedBy?: {
    id: number
    username: string
  }
  movementDate: string
  createdAt: string
}

export interface InventoryMovementParams {
  productId?: number
  fromWarehouseId?: number
  toWarehouseId?: number
  movementType?: string
  performedBy?: number
  startDate?: string
  endDate?: string
  referenceNumber?: string
}
