import { useMutation, useQueryClient } from "@tanstack/react-query"
import { stockAdjustmentKeys } from "./keys"
import { rejectStockAdjustment } from "./api"

export function useRejectStockAdjustment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, reason }: { id: number; reason: string }) => rejectStockAdjustment(id, reason),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: stockAdjustmentKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: stockAdjustmentKeys.lists() })
      queryClient.invalidateQueries({ queryKey: stockAdjustmentKeys.pending() })
    },
  })
}
