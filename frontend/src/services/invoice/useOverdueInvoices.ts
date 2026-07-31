import { useQuery } from "@tanstack/react-query"
import { invoiceKeys } from "./keys"
import { getOverdueInvoices } from "./api"

export function useOverdueInvoices() {
  return useQuery({
    queryKey: invoiceKeys.overdue(),
    queryFn: () => getOverdueInvoices(),
  })
}
