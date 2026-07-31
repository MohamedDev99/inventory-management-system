import { useQuery } from "@tanstack/react-query"
import { reportKeys } from "./keys"
import { getReportTypes } from "./api"

export function useReportTypes() {
  return useQuery({
    queryKey: reportKeys.types(),
    queryFn: () => getReportTypes(),
  })
}
