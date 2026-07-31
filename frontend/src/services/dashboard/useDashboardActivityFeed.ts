import { useQuery } from "@tanstack/react-query"
import { dashboardKeys } from "./keys"
import { getDashboardActivityFeed } from "./api"

export function useDashboardActivityFeed(limit: number = 20) {
  return useQuery({
    queryKey: [...dashboardKeys.activityFeed(), limit],
    queryFn: () => getDashboardActivityFeed(limit),
  })
}
