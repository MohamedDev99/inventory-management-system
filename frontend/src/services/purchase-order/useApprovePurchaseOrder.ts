import { useMutation, useQueryClient } from "@tanstack/react-query"
import { purchaseOrderKeys } from "./keys"
import { approvePurchaseOrder } from "./api"
import { toast } from "sonner"

export function useApprovePurchaseOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => approvePurchaseOrder(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: purchaseOrderKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: purchaseOrderKeys.lists() })
      queryClient.invalidateQueries({ queryKey: purchaseOrderKeys.pendingApproval() })
      toast.success("Purchase order approved")
    },
    onError: () => {
      toast.error("Failed to approve purchase order")
    },
  })
}
