import { useQuery } from "@tanstack/react-query"
import { auditLogKeys } from "./keys"
import { getAuditLogsByEntity } from "./api"

export function useAuditLogsByEntity(entityType: string, entityId: number) {
  return useQuery({
    queryKey: auditLogKeys.byEntity(entityType, entityId),
    queryFn: () => getAuditLogsByEntity(entityType, entityId),
    enabled: !!entityType && !!entityId,
  })
}
