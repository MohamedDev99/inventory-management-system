import { useMutation, useQueryClient } from "@tanstack/react-query"
import { salesOrderKeys } from "./keys"
import { confirmSalesOrder } from "./api"
import { toast } from "sonner"

export function useConfirmSalesOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => confirmSalesOrder(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.lists() })
      toast.success("Sales order confirmed")
    },
    onError: () => {
      toast.error("Failed to confirm sales order")
    },
  })
}
