import { useQuery } from "@tanstack/react-query"
import { reportKeys } from "./keys"
import { getReport } from "./api"

export function useReport(id: number) {
  return useQuery({
    queryKey: reportKeys.detail(id),
    queryFn: () => getReport(id),
    enabled: !!id,
    refetchInterval: (query) => {
      const data = query.state.data?.data
      if (data?.status === 'PENDING') return 5000 // Poll every 5 seconds while pending
      return false
    },
  })
}
