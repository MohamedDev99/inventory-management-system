import { useQuery } from "@tanstack/react-query"
import { salesOrderKeys } from "./keys"
import { getSalesOrderInvoice } from "./api"

export function useSalesOrderInvoice(id: number) {
  return useQuery({
    queryKey: salesOrderKeys.invoice(id),
    queryFn: () => getSalesOrderInvoice(id),
    enabled: !!id,
  })
}
