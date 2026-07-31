import { useQuery } from "@tanstack/react-query"
import { reportKeys } from "./keys"
import { downloadReport } from "./api"

export function useDownloadReport(id: number) {
  return useQuery({
    queryKey: [...reportKeys.detail(id), 'download'],
    queryFn: () => downloadReport(id),
    enabled: !!id,
  })
}
