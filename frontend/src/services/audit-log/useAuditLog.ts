import { useQuery } from "@tanstack/react-query"
import { auditLogKeys } from "./keys"
import { getAuditLog } from "./api"

export function useAuditLog(id: number) {
  return useQuery({
    queryKey: auditLogKeys.detail(id),
    queryFn: () => getAuditLog(id),
    enabled: !!id,
  })
}
