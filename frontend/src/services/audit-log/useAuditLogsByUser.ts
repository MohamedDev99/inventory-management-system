import { useQuery } from "@tanstack/react-query"
import { auditLogKeys } from "./keys"
import { getAuditLogsByUser } from "./api"
import type { PageParams } from "@/types"

export function useAuditLogsByUser(userId: number, params: PageParams = {}) {
  return useQuery({
    queryKey: [...auditLogKeys.byUser(userId), params],
    queryFn: () => getAuditLogsByUser(userId, params),
    enabled: !!userId,
  })
}
