// Sales Order Query Keys
import type { PageParams } from "@/types"

export const salesOrderKeys = {
  all: ['salesOrders'] as const,
  lists: () => [...salesOrderKeys.all, 'list'] as const,
  list: (params: PageParams = {}) => [...salesOrderKeys.lists(), params] as const,
  details: () => [...salesOrderKeys.all, 'detail'] as const,
  detail: (id: number) => [...salesOrderKeys.details(), id] as const,
  invoice: (id: number) => [...salesOrderKeys.detail(id), 'invoice'] as const,
}
