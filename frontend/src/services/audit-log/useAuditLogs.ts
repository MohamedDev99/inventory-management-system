import { useQuery } from "@tanstack/react-query"
import { auditLogKeys } from "./keys"
import { getAuditLogs } from "./api"
import type { AuditLogParams } from "./api"

export function useAuditLogs(params: AuditLogParams = {}) {
  return useQuery({
    queryKey: auditLogKeys.list(params),
    queryFn: () => getAuditLogs(params),
  })
}
