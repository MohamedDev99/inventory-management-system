import { useQuery } from "@tanstack/react-query"
import { dashboardKeys } from "./keys"
import { getDashboardTopSellingProducts } from "./api"

export function useDashboardTopSellingProducts(params: { period?: "WEEK" | "MONTH" | "QUARTER" | "YEAR"; limit?: number } = {}) {
  return useQuery({
    queryKey: [...dashboardKeys.topSellingProducts(), params],
    queryFn: () => getDashboardTopSellingProducts(params),
  })
}
