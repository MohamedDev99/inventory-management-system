import { useQuery } from "@tanstack/react-query"
import { dashboardKeys } from "./keys"
import { getDashboardInventorySummary } from "./api"

export function useDashboardInventorySummary(params: { warehouseId?: number; categoryId?: number } = {}) {
  return useQuery({
    queryKey: [...dashboardKeys.inventorySummary(), params],
    queryFn: () => getDashboardInventorySummary(params),
  })
}
