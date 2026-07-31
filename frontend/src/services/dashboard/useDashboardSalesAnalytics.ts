import { useQuery } from "@tanstack/react-query"
import { dashboardKeys } from "./keys"
import { getDashboardSalesAnalytics } from "./api"

export function useDashboardSalesAnalytics(params: { period?: "TODAY" | "WEEK" | "MONTH" | "QUARTER" | "YEAR"; startDate?: string; endDate?: string } = {}) {
  return useQuery({
    queryKey: [...dashboardKeys.salesAnalytics(), params],
    queryFn: () => getDashboardSalesAnalytics(params),
  })
}
