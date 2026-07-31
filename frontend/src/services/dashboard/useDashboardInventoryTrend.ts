import { useQuery } from "@tanstack/react-query"
import { dashboardKeys } from "./keys"
import { getDashboardInventoryTrend } from "./api"

export function useDashboardInventoryTrend(params: { period?: "WEEK" | "MONTH" | "QUARTER" | "YEAR"; warehouseId?: number } = {}) {
  return useQuery({
    queryKey: [...dashboardKeys.inventoryTrend(), params],
    queryFn: () => getDashboardInventoryTrend(params),
  })
}
