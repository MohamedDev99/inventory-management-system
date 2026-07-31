// Stock Adjustment Query Keys
import type { PageParams } from "@/types"

export const stockAdjustmentKeys = {
  all: ['stockAdjustments'] as const,
  lists: () => [...stockAdjustmentKeys.all, 'list'] as const,
  list: (params: PageParams = {}) => [...stockAdjustmentKeys.lists(), params] as const,
  details: () => [...stockAdjustmentKeys.all, 'detail'] as const,
  detail: (id: number) => [...stockAdjustmentKeys.details(), id] as const,
  pending: () => [...stockAdjustmentKeys.all, 'pending'] as const,
  reasons: () => [...stockAdjustmentKeys.all, 'reasons'] as const,
}
