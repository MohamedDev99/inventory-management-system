// Audit Log Query Keys
import type { PageParams } from "@/types"

export const auditLogKeys = {
  all: ['auditLogs'] as const,
  lists: () => [...auditLogKeys.all, 'list'] as const,
  list: (params: PageParams = {}) => [...auditLogKeys.lists(), params] as const,
  details: () => [...auditLogKeys.all, 'detail'] as const,
  detail: (id: number) => [...auditLogKeys.details(), id] as const,
  byEntity: (entityType: string, entityId: number) => [...auditLogKeys.all, 'entity', entityType, entityId] as const,
  byUser: (userId: number) => [...auditLogKeys.all, 'user', userId] as const,
  summary: () => [...auditLogKeys.all, 'summary'] as const,
}
