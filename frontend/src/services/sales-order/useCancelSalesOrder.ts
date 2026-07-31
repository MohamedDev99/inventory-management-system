import { useMutation, useQueryClient } from "@tanstack/react-query"
import { salesOrderKeys } from "./keys"
import { cancelSalesOrder } from "./api"
import { toast } from "sonner"

export function useCancelSalesOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, reason }: { id: number; reason: string }) => 
      cancelSalesOrder(id, reason, 1), // Default userId, adjust as needed
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.lists() })
      toast.success("Sales order cancelled")
    },
    onError: () => {
      toast.error("Failed to cancel sales order")
    },
  })
}
