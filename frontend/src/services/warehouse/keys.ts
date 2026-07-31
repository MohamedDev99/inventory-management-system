// Warehouse Query Keys
import type { PageParams } from "@/types"

export const warehouseKeys = {
  all: ['warehouses'] as const,
  lists: () => [...warehouseKeys.all, 'list'] as const,
  list: (params: PageParams = {}) => [...warehouseKeys.lists(), params] as const,
  details: () => [...warehouseKeys.all, 'detail'] as const,
  detail: (id: number) => [...warehouseKeys.details(), id] as const,
  inventory: (id: number) => [...warehouseKeys.detail(id), 'inventory'] as const,
  stats: (id: number) => [...warehouseKeys.detail(id), 'stats'] as const,
}
