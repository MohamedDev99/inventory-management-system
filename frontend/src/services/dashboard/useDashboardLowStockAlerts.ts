import { useQuery } from "@tanstack/react-query"
import { dashboardKeys } from "./keys"
import { getDashboardLowStockAlerts } from "./api"

export function useDashboardLowStockAlerts() {
  return useQuery({
    queryKey: dashboardKeys.lowStockAlerts(),
    queryFn: () => getDashboardLowStockAlerts(),
    refetchInterval: 60000, // Refresh every minute
  })
}
