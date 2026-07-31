import { useQuery } from "@tanstack/react-query"
import { auditLogKeys } from "./keys"
import { getAuditLogSummary } from "./api"

export function useAuditLogSummary(params: { startDate?: string; endDate?: string; groupBy?: "USER" | "ACTION" | "ENTITY_TYPE" | "DAY" } = {}) {
  return useQuery({
    queryKey: [...auditLogKeys.summary(), params],
    queryFn: () => getAuditLogSummary(params),
  })
}
