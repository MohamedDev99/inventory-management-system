import { useQuery } from "@tanstack/react-query"
import { stockAdjustmentKeys } from "./keys"
import { getPendingStockAdjustments } from "./api"

export function usePendingStockAdjustments() {
  return useQuery({
    queryKey: stockAdjustmentKeys.pending(),
    queryFn: () => getPendingStockAdjustments(),
  })
}
