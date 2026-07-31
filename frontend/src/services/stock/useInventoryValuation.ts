import { useQuery } from "@tanstack/react-query"
import { stockKeys } from "./keys"
import { getInventoryValuation } from "./api"

export function useInventoryValuation(params: { warehouseId?: number; categoryId?: number; valuationType?: "COST" | "RETAIL" } = {}) {
  return useQuery({
    queryKey: [...stockKeys.valuation(), params],
    queryFn: () => getInventoryValuation(params),
  })
}
