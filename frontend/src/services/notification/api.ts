import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import type { PageParams } from "@/types"

// Notification schema (simple version)
const notificationSchema = z.object({
  id: z.number(),
  title: z.string(),
  message: z.string(),
  notificationType: z.string(),
  priority: z.enum(["LOW", "MEDIUM", "HIGH", "CRITICAL"]),
  isRead: z.boolean(),
  createdAt: z.string().datetime(),
  readAt: z.string().datetime().nullable().optional(),
})

export type Notification = z.infer<typeof notificationSchema>

export interface NotificationParams extends PageParams {
  isRead?: boolean
  notificationType?: string
  priority?: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL"
  startDate?: string
  endDate?: string
}

// GET /api/notifications - Paginated list
export async function getNotifications(params: NotificationParams = {}) {
  const response = await api.get("/notifications", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(notificationSchema)),
    response.data,
    { prefix: "Get Notifications Response" }
  )
  return validated
}

// GET /api/notifications/{id} - Notification detail
export async function getNotification(id: number) {
  const response = await api.get(`/notifications/${id}`)
  const validated = validateResponse(
    apiResponseSchema(notificationSchema),
    response.data,
    { prefix: "Get Notification Response" }
  )
  return validated
}

// GET /api/notifications/unread - Unread count
export async function getUnreadNotificationCount() {
  const response = await api.get("/notifications/unread")
  // Return raw response for complex nested objects
  return response.data
}

// PATCH /api/notifications/{id}/read - Mark as read
export async function markNotificationAsRead(id: number) {
  const response = await api.patch(`/notifications/${id}/read`)
  const validated = validateResponse(
    apiResponseSchema(notificationSchema),
    response.data,
    { prefix: "Mark Notification as Read Response" }
  )
  return validated
}

// PATCH /api/notifications/{id}/unread - Mark as unread
export async function markNotificationAsUnread(id: number) {
  const response = await api.patch(`/notifications/${id}/unread`)
  const validated = validateResponse(
    apiResponseSchema(notificationSchema),
    response.data,
    { prefix: "Mark Notification as Unread Response" }
  )
  return validated
}

// PATCH /api/notifications/read-all - Mark all as read
export async function markAllNotificationsAsRead() {
  const response = await api.patch("/notifications/read-all")
  const validated = validateResponse(
    apiResponseSchema(z.object({ markedAsRead: z.number() })),
    response.data,
    { prefix: "Mark All as Read Response" }
  )
  return validated
}

// DELETE /api/notifications/{id} - Delete notification
export async function deleteNotification(id: number): Promise<void> {
  await api.delete(`/notifications/${id}`)
}

// DELETE /api/notifications/clear-read - Clear read notifications
export async function clearReadNotifications() {
  const response = await api.delete("/notifications/clear-read")
  const validated = validateResponse(
    apiResponseSchema(z.object({ deletedCount: z.number() })),
    response.data,
    { prefix: "Clear Read Notifications Response" }
  )
  return validated
}

// GET /api/notifications/preferences - Get preferences
export async function getNotificationPreferences() {
  const response = await api.get("/notifications/preferences")
  // Return raw response for preferences
  return response.data
}

// PUT /api/notifications/preferences - Update preferences
export async function updateNotificationPreferences(data: {
  emailNotifications: boolean
  lowStockAlerts: boolean
  orderNotifications: boolean
  reportNotifications: boolean
}) {
  const response = await api.put("/notifications/preferences", data)
  // Return raw response
  return response.data
}
