import { useQuery } from "@tanstack/react-query"
import { salesOrderKeys } from "./keys"
import { getSalesOrderPdf } from "./api"

export function useSalesOrderPdf(id: number) {
  return useQuery({
    queryKey: [...salesOrderKeys.detail(id), 'pdf'],
    queryFn: () => getSalesOrderPdf(id),
    enabled: !!id,
  })
}
