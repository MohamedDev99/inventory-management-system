import { useMutation, useQueryClient } from "@tanstack/react-query"
import { salesOrderKeys } from "./keys"
import { deleteSalesOrder } from "./api"
import { toast } from "sonner"

export function useDeleteSalesOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => deleteSalesOrder(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.lists() })
      toast.success("Sales order deleted successfully")
    },
    onError: () => {
      toast.error("Failed to delete sales order")
    },
  })
}
