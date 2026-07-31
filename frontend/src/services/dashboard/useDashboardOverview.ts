import { useQuery } from "@tanstack/react-query"
import { dashboardKeys } from "./keys"
import { getDashboardOverview } from "./api"

export function useDashboardOverview() {
  return useQuery({
    queryKey: dashboardKeys.overview(),
    queryFn: () => getDashboardOverview(),
    refetchInterval: 30000, // Refresh every 30 seconds
  })
}
