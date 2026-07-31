// Product Query Keys
import type { PageParams } from "@/types"

export const productKeys = {
  all: ['products'] as const,
  lists: () => [...productKeys.all, 'list'] as const,
  list: (params: PageParams = {}) => [...productKeys.lists(), params] as const,
  details: () => [...productKeys.all, 'detail'] as const,
  detail: (id: number) => [...productKeys.details(), id] as const,
  bySku: (sku: string) => [...productKeys.all, 'sku', sku] as const,
  byBarcode: (barcode: string) => [...productKeys.all, 'barcode', barcode] as const,
  lowStock: () => [...productKeys.all, 'lowStock'] as const,
  inventory: (id: number) => [...productKeys.detail(id), 'inventory'] as const,
  movements: (id: number) => [...productKeys.detail(id), 'movements'] as const,
}
