import { useMutation, useQueryClient } from "@tanstack/react-query"
import { purchaseOrderKeys } from "./keys"
import { rejectPurchaseOrder } from "./api"
import { toast } from "sonner"

export function useRejectPurchaseOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, reason }: { id: number; reason: string }) => rejectPurchaseOrder(id, reason),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: purchaseOrderKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: purchaseOrderKeys.lists() })
      queryClient.invalidateQueries({ queryKey: purchaseOrderKeys.pendingApproval() })
      toast.success("Purchase order rejected")
    },
    onError: () => {
      toast.error("Failed to reject purchase order")
    },
  })
}
