import { useMutation, useQueryClient } from "@tanstack/react-query"
import { salesOrderKeys } from "./keys"
import { updateSalesOrder, type CreateSalesOrderRequest } from "./api"

export function useUpdateSalesOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: CreateSalesOrderRequest }) =>
      updateSalesOrder(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.lists() })
    },
  })
}
