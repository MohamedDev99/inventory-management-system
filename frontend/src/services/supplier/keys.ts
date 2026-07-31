// Supplier Query Keys
import type { PageParams } from "@/types"

export const supplierKeys = {
  all: ['suppliers'] as const,
  lists: () => [...supplierKeys.all, 'list'] as const,
  list: (params: PageParams = {}) => [...supplierKeys.lists(), params] as const,
  details: () => [...supplierKeys.all, 'detail'] as const,
  detail: (id: number) => [...supplierKeys.details(), id] as const,
  orders: (id: number) => [...supplierKeys.detail(id), 'orders'] as const,
  performance: (id: number) => [...supplierKeys.detail(id), 'performance'] as const,
}
