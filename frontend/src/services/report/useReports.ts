import { useQuery } from "@tanstack/react-query"
import { reportKeys } from "./keys"
import { getReports } from "./api"
import type { ReportParams } from "./api"

export function useReports(params: ReportParams = {}) {
  return useQuery({
    queryKey: reportKeys.list(params),
    queryFn: () => getReports(params),
  })
}
