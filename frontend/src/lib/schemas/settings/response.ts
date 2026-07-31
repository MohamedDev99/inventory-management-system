import { z } from "zod"

// ========================================
// SETTINGS RESPONSE SCHEMAS
// ========================================

// Profile settings response
export const profileSettingsResponseSchema = z.object({
  firstName: z.string(),
  lastName: z.string(),
  email: z.string().email().optional(),
  phone: z.string().optional(),
  jobTitle: z.string().optional(),
  address: z.string().optional(),
  city: z.string().optional(),
  state: z.string().optional(),
  postalCode: z.string().optional(),
  avatar: z.string().optional(),
})

export type ProfileSettingsResponse = z.infer<typeof profileSettingsResponseSchema>

// Business settings response
export const businessSettingsResponseSchema = z.object({
  companyName: z.string().optional(),
  industry: z.string().optional(),
  businessType: z.string().optional(),
  fiscalYearStart: z.string().optional(),
  currency: z.string().optional(),
  timezone: z.string().optional(),
  logo: z.string().optional(),
})

export type BusinessSettingsResponse = z.infer<typeof businessSettingsResponseSchema>

// Notification settings response
export const notificationSettingsResponseSchema = z.object({
  emailNotifications: z.boolean(),
  lowStockAlerts: z.boolean(),
  orderNotifications: z.boolean(),
  reportNotifications: z.boolean(),
})

export type NotificationSettingsResponse = z.infer<typeof notificationSettingsResponseSchema>

// All settings response
export const allSettingsResponseSchema = z.object({
  profile: profileSettingsResponseSchema,
  business: businessSettingsResponseSchema,
  notifications: notificationSettingsResponseSchema,
})

export type AllSettingsResponse = z.infer<typeof allSettingsResponseSchema>
