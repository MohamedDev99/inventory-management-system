import { useQuery } from "@tanstack/react-query"
import { purchaseOrderKeys } from "./keys"
import { getPurchaseOrder } from "./api"

export function usePurchaseOrder(id: number) {
  return useQuery({
    queryKey: purchaseOrderKeys.detail(id),
    queryFn: () => getPurchaseOrder(id),
    enabled: !!id,
  })
}
