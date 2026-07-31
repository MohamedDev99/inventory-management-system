import { useMutation, useQueryClient } from "@tanstack/react-query"
import { purchaseOrderKeys } from "./keys"
import { submitPurchaseOrder } from "./api"
import { toast } from "sonner"

export function useSubmitPurchaseOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => submitPurchaseOrder(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: purchaseOrderKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: purchaseOrderKeys.lists() })
      toast.success("Purchase order submitted")
    },
    onError: () => {
      toast.error("Failed to submit purchase order")
    },
  })
}
