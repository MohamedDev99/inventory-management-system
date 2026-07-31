import { useQuery } from "@tanstack/react-query"
import { invoiceKeys } from "./keys"
import { getInvoices } from "./api"
import type { InvoiceParams } from "./api"

export function useInvoices(params: InvoiceParams = {}) {
  return useQuery({
    queryKey: invoiceKeys.list(params),
    queryFn: () => getInvoices(params),
  })
}
