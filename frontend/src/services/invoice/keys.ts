// Invoice Query Keys
import type { PageParams } from "@/types"

export const invoiceKeys = {
  all: ['invoices'] as const,
  lists: () => [...invoiceKeys.all, 'list'] as const,
  list: (params: PageParams = {}) => [...invoiceKeys.lists(), params] as const,
  details: () => [...invoiceKeys.all, 'detail'] as const,
  detail: (id: number) => [...invoiceKeys.details(), id] as const,
  overdue: () => [...invoiceKeys.all, 'overdue'] as const,
}
