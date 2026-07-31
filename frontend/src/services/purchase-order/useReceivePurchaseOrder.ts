import { useMutation, useQueryClient } from "@tanstack/react-query"
import { purchaseOrderKeys } from "./keys"
import { receivePurchaseOrder, type ReceivePurchaseOrderRequest } from "./api"
import { toast } from "sonner"

export function useReceivePurchaseOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: ReceivePurchaseOrderRequest }) =>
      receivePurchaseOrder(id, data, 1), // Default userId, adjust as needed
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: purchaseOrderKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: purchaseOrderKeys.lists() })
      toast.success("Purchase order received")
    },
    onError: () => {
      toast.error("Failed to receive purchase order")
    },
  })
}
