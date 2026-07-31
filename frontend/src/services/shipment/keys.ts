// Shipment Query Keys
import type { PageParams } from "@/types"

export const shipmentKeys = {
  all: ['shipments'] as const,
  lists: () => [...shipmentKeys.all, 'list'] as const,
  list: (params: PageParams = {}) => [...shipmentKeys.lists(), params] as const,
  details: () => [...shipmentKeys.all, 'detail'] as const,
  detail: (id: number) => [...shipmentKeys.details(), id] as const,
  tracking: (id: number) => [...shipmentKeys.detail(id), 'tracking'] as const,
  byTracking: (trackingNumber: string) => [...shipmentKeys.all, 'tracking', trackingNumber] as const,
}
