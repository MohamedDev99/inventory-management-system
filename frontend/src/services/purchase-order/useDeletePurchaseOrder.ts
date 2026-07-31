import { useMutation, useQueryClient } from "@tanstack/react-query"
import { purchaseOrderKeys } from "./keys"
import { deletePurchaseOrder } from "./api"
import { toast } from "sonner"

export function useDeletePurchaseOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => deletePurchaseOrder(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: purchaseOrderKeys.lists() })
      toast.success("Purchase order deleted successfully")
    },
    onError: () => {
      toast.error("Failed to delete purchase order")
    },
  })
}
