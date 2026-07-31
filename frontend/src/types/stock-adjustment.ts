// ========================================
// STOCK ADJUSTMENT TYPES
// ========================================

export interface StockAdjustment {
  id: number
  product: {
    id: number
    sku: string
    name: string
  }
  warehouse: {
    id: number
    name: string
  }
  quantityBefore: number
  quantityAfter: number
  quantityChange: number
  adjustmentType: "ADD" | "REMOVE" | "CORRECTION"
  reason: string
  status: "PENDING" | "APPROVED" | "REJECTED"
  notes?: string
  performedBy?: {
    id: number
    username: string
  }
  approvedBy?: {
    id: number
    username: string
  }
  adjustmentDate: string
  createdAt: string
}

export interface StockAdjustmentParams {
  productId?: number
  warehouseId?: number
  status?: "PENDING" | "APPROVED" | "REJECTED"
  adjustmentType?: "ADD" | "REMOVE" | "CORRECTION"
  reason?: string
  performedBy?: number
  startDate?: string
  endDate?: string
}

export interface CreateStockAdjustmentRequest {
  productId: number
  warehouseId: number
  quantityChange: number
  adjustmentType: "ADD" | "REMOVE" | "CORRECTION"
  reason: string
  notes?: string
}
