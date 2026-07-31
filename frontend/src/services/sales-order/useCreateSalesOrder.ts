import { useMutation, useQueryClient } from "@tanstack/react-query"
import { salesOrderKeys } from "./keys"
import { createSalesOrder, type CreateSalesOrderRequest } from "./api"
import { toast } from "sonner"

export function useCreateSalesOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ data, createdByUserId }: { data: CreateSalesOrderRequest; createdByUserId: number }) => 
      createSalesOrder(data, createdByUserId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.lists() })
      toast.success("Sales order created successfully")
    },
    onError: () => {
      toast.error("Failed to create sales order")
    },
  })
}
