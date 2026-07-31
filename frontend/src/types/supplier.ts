// ========================================
// SUPPLIER TYPES
// ========================================

export interface Supplier {
  id: number
  name: string
  code: string
  contactPerson?: string
  email?: string
  phone?: string
  address?: string
  city?: string
  country?: string
  paymentTerms?: string
  rating?: number
  isActive: boolean
  stats?: SupplierStats
  performance?: SupplierPerformance
  createdAt: string
  updatedAt?: string
}

export interface SupplierStats {
  totalOrders: number
  totalSpent: number
  averageOrderValue: number
  onTimeDeliveryRate: number
}

export interface SupplierPerformance {
  totalOrders: number
  totalSpent: number
  averageOrderValue: number
  onTimeDeliveryRate: number
  averageDeliveryDays: number
  cancelledOrders: number
  qualityIssues: number
}
