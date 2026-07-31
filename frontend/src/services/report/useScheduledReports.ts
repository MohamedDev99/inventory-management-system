import { useQuery } from "@tanstack/react-query"
import { reportKeys } from "./keys"
import { getScheduledReports } from "./api"

export function useScheduledReports() {
  return useQuery({
    queryKey: reportKeys.scheduled(),
    queryFn: () => getScheduledReports(),
  })
}
