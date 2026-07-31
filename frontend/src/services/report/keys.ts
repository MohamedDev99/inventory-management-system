// Report Query Keys
import type { PageParams } from "@/types"

export const reportKeys = {
  all: ['reports'] as const,
  lists: () => [...reportKeys.all, 'list'] as const,
  list: (params: PageParams = {}) => [...reportKeys.lists(), params] as const,
  details: () => [...reportKeys.all, 'detail'] as const,
  detail: (id: number) => [...reportKeys.details(), id] as const,
  types: () => [...reportKeys.all, 'types'] as const,
  scheduled: () => [...reportKeys.all, 'scheduled'] as const,
}
