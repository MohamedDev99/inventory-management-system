// Stock (Inventory) Query Keys
import type { PageParams } from "@/types"

export const stockKeys = {
  all: ['stock'] as const,
  lists: () => [...stockKeys.all, 'list'] as const,
  list: (params: PageParams = {}) => [...stockKeys.lists(), params] as const,
  details: () => [...stockKeys.all, 'detail'] as const,
  detail: (id: number) => [...stockKeys.details(), id] as const,
  byWarehouse: (warehouseId: number) => [...stockKeys.all, 'warehouse', warehouseId] as const,
  byProduct: (productId: number) => [...stockKeys.all, 'product', productId] as const,
  valuation: () => [...stockKeys.all, 'valuation'] as const,
}
