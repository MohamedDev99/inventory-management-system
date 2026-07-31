import { useMutation, useQueryClient } from "@tanstack/react-query"
import { stockAdjustmentKeys } from "./keys"
import { approveStockAdjustment } from "./api"

export function useApproveStockAdjustment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, notes }: { id: number; notes?: string }) => approveStockAdjustment(id, notes),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: stockAdjustmentKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: stockAdjustmentKeys.lists() })
      queryClient.invalidateQueries({ queryKey: stockAdjustmentKeys.pending() })
    },
  })
}
