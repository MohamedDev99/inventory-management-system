import { useQuery } from "@tanstack/react-query"
import { inventoryMovementKeys } from "./keys"
import { getInventoryMovementSummary } from "./api"

export function useInventoryMovementSummary(params: { startDate?: string; endDate?: string; warehouseId?: number; groupBy?: "DAY" | "WEEK" | "MONTH" } = {}) {
  return useQuery({
    queryKey: [...inventoryMovementKeys.summary(), params],
    queryFn: () => getInventoryMovementSummary(params),
  })
}
