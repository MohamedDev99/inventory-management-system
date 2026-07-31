import { useMutation, useQueryClient } from "@tanstack/react-query"
import { purchaseOrderKeys } from "./keys"
import { createPurchaseOrder, type CreatePurchaseOrderRequest } from "./api"
import { toast } from "sonner"

export function useCreatePurchaseOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreatePurchaseOrderRequest) => 
      createPurchaseOrder(data, 1), // Default userId, adjust as needed
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: purchaseOrderKeys.lists() })
      toast.success("Purchase order created successfully")
    },
    onError: () => {
      toast.error("Failed to create purchase order")
    },
  })
}
