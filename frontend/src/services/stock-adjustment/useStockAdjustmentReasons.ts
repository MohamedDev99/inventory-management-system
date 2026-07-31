import { useQuery } from "@tanstack/react-query"
import { stockAdjustmentKeys } from "./keys"
import { getStockAdjustmentReasons } from "./api"

export function useStockAdjustmentReasons() {
  return useQuery({
    queryKey: stockAdjustmentKeys.reasons(),
    queryFn: () => getStockAdjustmentReasons(),
  })
}
