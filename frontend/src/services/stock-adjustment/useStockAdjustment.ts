import { useQuery } from "@tanstack/react-query"
import { stockAdjustmentKeys } from "./keys"
import { getStockAdjustment } from "./api"

export function useStockAdjustment(id: number) {
  return useQuery({
    queryKey: stockAdjustmentKeys.detail(id),
    queryFn: () => getStockAdjustment(id),
    enabled: !!id,
  })
}
