import { useMutation, useQueryClient } from "@tanstack/react-query"
import { salesOrderKeys } from "./keys"
import { deliverSalesOrder } from "./api"
import { toast } from "sonner"

export function useDeliverSalesOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) =>
      deliverSalesOrder(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.lists() })
      toast.success("Sales order delivered")
    },
    onError: () => {
      toast.error("Failed to deliver sales order")
    },
  })
}
