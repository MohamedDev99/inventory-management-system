import { useQuery } from "@tanstack/react-query"
import { dashboardKeys } from "./keys"
import { getDashboardPendingActions } from "./api"

export function useDashboardPendingActions() {
  return useQuery({
    queryKey: dashboardKeys.pendingActions(),
    queryFn: () => getDashboardPendingActions(),
  })
}
