// ========================================
// DASHBOARD TYPES
// ========================================

export interface DashboardStats {
  totalProducts: number
  totalValue: number
  lowStockCount: number
  outOfStockCount: number
  totalOrders: number
  pendingApprovals: number
}

export interface ChartData {
  name: string
  value: number
  [key: string]: string | number
}
