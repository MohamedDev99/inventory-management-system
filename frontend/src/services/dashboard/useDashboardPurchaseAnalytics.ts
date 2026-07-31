import { useQuery } from "@tanstack/react-query"
import { dashboardKeys } from "./keys"
import { getDashboardPurchaseAnalytics } from "./api"

export function useDashboardPurchaseAnalytics(params: { period?: "TODAY" | "WEEK" | "MONTH" | "QUARTER" | "YEAR"; startDate?: string; endDate?: string } = {}) {
  return useQuery({
    queryKey: [...dashboardKeys.purchaseAnalytics(), params],
    queryFn: () => getDashboardPurchaseAnalytics(params),
  })
}
