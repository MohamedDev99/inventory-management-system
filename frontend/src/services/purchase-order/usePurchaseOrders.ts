import { useQuery } from "@tanstack/react-query"
import { purchaseOrderKeys } from "./keys"
import { getPurchaseOrders } from "./api"
import type { PurchaseOrderParams } from "./api"

export function usePurchaseOrders(params: PurchaseOrderParams = {}) {
  return useQuery({
    queryKey: purchaseOrderKeys.list(params),
    queryFn: () => getPurchaseOrders(params),
  })
}
