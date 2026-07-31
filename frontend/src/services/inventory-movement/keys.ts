// Inventory Movement Query Keys
import type { PageParams } from "@/types"

export const inventoryMovementKeys = {
  all: ['inventoryMovements'] as const,
  lists: () => [...inventoryMovementKeys.all, 'list'] as const,
  list: (params: PageParams = {}) => [...inventoryMovementKeys.lists(), params] as const,
  details: () => [...inventoryMovementKeys.all, 'detail'] as const,
  detail: (id: number) => [...inventoryMovementKeys.details(), id] as const,
  byProduct: (productId: number) => [...inventoryMovementKeys.all, 'product', productId] as const,
  byWarehouse: (warehouseId: number) => [...inventoryMovementKeys.all, 'warehouse', warehouseId] as const,
  summary: () => [...inventoryMovementKeys.all, 'summary'] as const,
}
