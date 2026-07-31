// User Query Keys
import type { PageParams } from "@/types"

export const userKeys = {
  all: ['users'] as const,
  lists: () => [...userKeys.all, 'list'] as const,
  list: (params: PageParams = {}) => [...userKeys.lists(), params] as const,
  details: () => [...userKeys.all, 'detail'] as const,
  detail: (id: number) => [...userKeys.details(), id] as const,
  me: () => [...userKeys.all, 'me'] as const,
  warehouses: (id: number) => [...userKeys.detail(id), 'warehouses'] as const,
}
