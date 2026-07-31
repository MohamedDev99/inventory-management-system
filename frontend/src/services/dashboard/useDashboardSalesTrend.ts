import { useQuery } from "@tanstack/react-query"
import { dashboardKeys } from "./keys"
import { getDashboardSalesTrend } from "./api"

export function useDashboardSalesTrend(params: { period?: "WEEK" | "MONTH" | "QUARTER" | "YEAR" } = {}) {
  return useQuery({
    queryKey: [...dashboardKeys.salesTrend(), params],
    queryFn: () => getDashboardSalesTrend(params),
  })
}
