import { useQuery } from "@tanstack/react-query"
import { invoiceKeys } from "./keys"
import { getInvoicePdf } from "./api"

export function useInvoicePdf(id: number) {
  return useQuery({
    queryKey: [...invoiceKeys.detail(id), 'pdf'],
    queryFn: () => getInvoicePdf(id),
    enabled: !!id,
  })
}
