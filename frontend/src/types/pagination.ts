// ========================================
// PAGINATION TYPES
// ========================================

export interface PaginatedResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

export interface PageParams {
  page?: number
  size?: number
  sort?: string
  search?: string
  isActive?: boolean
  categoryId?: number
  stockStatus?: "IN_STOCK" | "LOW_STOCK" | "OUT_OF_STOCK"
}
