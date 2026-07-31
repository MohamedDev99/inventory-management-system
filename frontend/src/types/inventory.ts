// ========================================
// INVENTORY TYPES
// ========================================

export type StockStatus = "NORMAL" | "LOW" | "CRITICAL" | "OUT_OF_STOCK"

// Lighter product reference (as returned by API)
export interface InventoryProduct {
  id: number
  sku: string
  name: string
  unitPrice?: number
}

// Lighter warehouse reference (as returned by API)
export interface InventoryWarehouse {
  id: number
  name: string
  code: string
}

export interface InventoryItem {
  id: number
  productId?: number
  product?: InventoryProduct
  warehouseId?: number
  warehouse?: InventoryWarehouse
  quantity: number
  locationCode?: string
  reorderLevel?: number
  isLowStock?: boolean
  stockStatus?: StockStatus
  lastStockCheck?: string
  createdAt?: string
  updatedAt?: string
  version?: number
}

export interface StockTransfer {
  productId: number
  fromWarehouseId: number
  toWarehouseId: number
  quantity: number
  reason?: string
  performedBy?: number
}
