import { useQuery } from "@tanstack/react-query"
import { stockAdjustmentKeys } from "./keys"
import { getStockAdjustments } from "./api"
import type { StockAdjustmentParams } from "./api"

export function useStockAdjustments(params: StockAdjustmentParams = {}) {
  return useQuery({
    queryKey: stockAdjustmentKeys.list(params),
    queryFn: () => getStockAdjustments(params),
  })
}
