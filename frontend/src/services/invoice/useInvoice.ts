import { useQuery } from "@tanstack/react-query"
import { invoiceKeys } from "./keys"
import { getInvoice } from "./api"

export function useInvoice(id: number) {
  return useQuery({
    queryKey: invoiceKeys.detail(id),
    queryFn: () => getInvoice(id),
    enabled: !!id,
  })
}
