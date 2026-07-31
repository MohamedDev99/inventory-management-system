import { useQuery } from "@tanstack/react-query"
import { salesOrderKeys } from "./keys"
import { getSalesOrder } from "./api"

export function useSalesOrder(id: number) {
  return useQuery({
    queryKey: salesOrderKeys.detail(id),
    queryFn: () => getSalesOrder(id),
    enabled: !!id,
  })
}
