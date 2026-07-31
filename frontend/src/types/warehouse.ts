// ========================================
// WAREHOUSE TYPES
// ========================================

// Lighter user reference for manager (as returned by API)
export interface WarehouseManager {
  id: number
  username: string
  email: string
}

export interface Warehouse {
  id: number
  name: string
  code: string
  address?: string
  city?: string
  state?: string
  country?: string
  postalCode?: string
  manager?: WarehouseManager
  capacity: number
  currentUtilization?: number
  utilizationPercentage?: number
  isActive: boolean
  stats?: WarehouseStats
  version?: number
  createdAt: string
  updatedAt?: string
}

export interface WarehouseStats {
  totalProducts: number
  totalValue: number
  capacity: number
  utilized: number
  utilizationPercentage: number
  lowStockCount: number
  outOfStockCount: number
}
