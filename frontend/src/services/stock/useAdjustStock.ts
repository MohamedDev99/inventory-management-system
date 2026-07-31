import { useMutation, useQueryClient } from "@tanstack/react-query"
import { stockKeys } from "./keys"
import { adjustStock } from "./api"
import type { StockAdjustmentRequest } from "@/lib/schemas/inventory/request"

export function useAdjustStock() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: StockAdjustRequest) => {
      if (!data.performedBy) {
        throw new Error("performedBy is required")
      }
      if (!data.reason) {
        throw new Error("reason is required (DAMAGED, EXPIRED, THEFT, COUNT_ERROR, RETURN, OTHER)")
      }
      return adjustStock(data)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: stockKeys.lists() })
    },
  })
}
