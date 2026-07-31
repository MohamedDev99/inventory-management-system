import { useMutation, useQueryClient } from "@tanstack/react-query"
import { stockAdjustmentKeys } from "./keys"
import { createStockAdjustment, type CreateStockAdjustmentRequest } from "./api"

export function useCreateStockAdjustment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreateStockAdjustmentRequest) => createStockAdjustment(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: stockAdjustmentKeys.lists() })
      queryClient.invalidateQueries({ queryKey: stockAdjustmentKeys.pending() })
    },
  })
}
