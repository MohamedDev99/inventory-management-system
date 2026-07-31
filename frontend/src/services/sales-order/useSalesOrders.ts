import { useQuery } from "@tanstack/react-query"
import { salesOrderKeys } from "./keys"
import { getSalesOrders } from "./api"
import type { SalesOrderParams } from "./api"

export function useSalesOrders(params: SalesOrderParams = {}) {
  return useQuery({
    queryKey: salesOrderKeys.list(params),
    queryFn: () => getSalesOrders(params),
  })
}
