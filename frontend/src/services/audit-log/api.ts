import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import type { PageParams } from "@/types"

// Audit log schema
const auditLogSchema = z.object({
  id: z.number(),
  entityType: z.string(),
  entityId: z.number(),
  action: z.enum(["CREATE", "UPDATE", "DELETE", "LOGIN", "LOGOUT", "APPROVE", "REJECT"]),
  oldValues: z.record(z.unknown()).optional(),
  newValues: z.record(z.unknown()).optional(),
  performedBy: z.object({
    id: z.number(),
    username: z.string(),
  }).optional(),
  ipAddress: z.string().optional(),
  userAgent: z.string().optional(),
  createdAt: z.string().datetime(),
})

export type AuditLog = z.infer<typeof auditLogSchema>

export interface AuditLogParams extends PageParams {
  entityType?: string
  entityId?: number
  action?: string
  performedBy?: number
  startDate?: string
  endDate?: string
  ipAddress?: string
}

// GET /api/audit-logs - Paginated list
export async function getAuditLogs(params: AuditLogParams = {}) {
  const response = await api.get("/audit-logs", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(auditLogSchema)),
    response.data,
    { prefix: "Get Audit Logs Response" }
  )
  return validated
}

// GET /api/audit-logs/{id} - Audit log detail
export async function getAuditLog(id: number) {
  const response = await api.get(`/audit-logs/${id}`)
  const validated = validateResponse(
    apiResponseSchema(auditLogSchema),
    response.data,
    { prefix: "Get Audit Log Response" }
  )
  return validated
}

// GET /api/audit-logs/entity/{entityType}/{entityId} - Logs for specific entity
export async function getAuditLogsByEntity(entityType: string, entityId: number) {
  const response = await api.get(`/audit-logs/entity/${entityType}/${entityId}`)
  const validated = validateResponse(
    apiResponseSchema(z.array(auditLogSchema)),
    response.data,
    { prefix: "Get Audit Logs by Entity Response" }
  )
  return validated
}

// GET /api/audit-logs/user/{userId} - Logs for specific user
export async function getAuditLogsByUser(userId: number, params: PageParams = {}) {
  const response = await api.get(`/audit-logs/user/${userId}`, { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(auditLogSchema)),
    response.data,
    { prefix: "Get Audit Logs by User Response" }
  )
  return validated
}

// GET /api/audit-logs/summary - Audit log summary
export async function getAuditLogSummary(params: { startDate?: string; endDate?: string; groupBy?: "USER" | "ACTION" | "ENTITY_TYPE" | "DAY" } = {}) {
  const response = await api.get("/audit-logs/summary", { params })
  // Return raw response for complex aggregation
  return response.data
}
