// ========================================
// PRODUCT TYPES
// ========================================

import type { CategoryListItem } from "./category"

export interface Product {
  id: number
  sku: string
  name: string
  description?: string
  category?: CategoryListItem  // API returns lighter category object
  unit: string
  unitPrice: number
  costPrice: number
  margin?: number
  marginPercentage?: number
  reorderLevel: number
  minStockLevel?: number
  barcode?: string
  imageUrl?: string
  isActive: boolean
  totalStock?: number
  availableStock?: number
  reservedStock?: number
  stockStatus?: "IN_STOCK" | "LOW_STOCK" | "OUT_OF_STOCK"
  createdAt?: string
  updatedAt?: string
  version?: number
}

export interface ProductFormData {
  sku: string
  name: string
  description?: string
  categoryId?: number
  unit?: string
  unitPrice: number
  costPrice: number
  reorderLevel?: number
  minStockLevel?: number
  barcode?: string
  imageUrl?: string
  isActive?: boolean
}
