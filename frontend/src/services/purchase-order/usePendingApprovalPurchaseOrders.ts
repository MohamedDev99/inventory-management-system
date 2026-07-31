import { useQuery } from "@tanstack/react-query"
import { purchaseOrderKeys } from "./keys"
import { getPendingApprovalPurchaseOrders } from "./api"

export function usePendingApprovalPurchaseOrders() {
  return useQuery({
    queryKey: purchaseOrderKeys.pendingApproval(),
    queryFn: () => getPendingApprovalPurchaseOrders(),
  })
}
