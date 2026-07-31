// Purchase Order Query Keys
import type { PageParams } from "@/types"

export const purchaseOrderKeys = {
  all: ['purchaseOrders'] as const,
  lists: () => [...purchaseOrderKeys.all, 'list'] as const,
  list: (params: PageParams = {}) => [...purchaseOrderKeys.lists(), params] as const,
  details: () => [...purchaseOrderKeys.all, 'detail'] as const,
  detail: (id: number) => [...purchaseOrderKeys.details(), id] as const,
  pendingApproval: () => [...purchaseOrderKeys.all, 'pendingApproval'] as const,
}
