import { useMutation, useQueryClient } from "@tanstack/react-query"
import { salesOrderKeys } from "./keys"
import { fulfillSalesOrder } from "./api"
import { toast } from "sonner"

export function useFulfillSalesOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => 
      fulfillSalesOrder(id, 1), // Default userId, adjust as needed
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.lists() })
      toast.success("Sales order fulfilled")
    },
    onError: () => {
      toast.error("Failed to fulfill sales order")
    },
  })
}
