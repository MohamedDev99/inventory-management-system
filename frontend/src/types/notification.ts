// ========================================
// NOTIFICATION TYPES
// ========================================

export type NotificationType = 
  | "LOW_STOCK" 
  | "ORDER_APPROVED" 
  | "ORDER_RECEIVED" 
  | "SHIPMENT" 
  | "STOCK_ADJUSTMENT" 
  | "SYSTEM"

// API response type (notificationType is string from API)
export interface Notification {
  id: number
  title: string
  message: string
  notificationType: string  // API returns string, cast to NotificationType when needed
  priority: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL"
  isRead: boolean
  actionUrl?: string
  referenceType?: string
  referenceId?: number
  createdAt: string
  readAt?: string | null
}

export interface NotificationPreferences {
  channels: {
    email: boolean
    push: boolean
    sms: boolean
  }
  types: {
    [key in NotificationType]?: {
      enabled: boolean
      email: boolean
      push: boolean
      sms: boolean
    }
  }
  quietHours?: {
    enabled: boolean
    startTime: string
    endTime: string
  }
}
