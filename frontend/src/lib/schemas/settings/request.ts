import { z } from "zod"

// ========================================
// SETTINGS REQUEST SCHEMAS
// ========================================

// Profile settings request
export const profileSettingsRequestSchema = z.object({
  firstName: z.string().min(1, "First name is required"),
  lastName: z.string().min(1, "Last name is required"),
  email: z.string().email("Invalid email").optional().or(z.literal("")),
  phone: z.string().optional(),
  jobTitle: z.string().optional(),
  address: z.string().optional(),
  city: z.string().optional(),
  state: z.string().optional(),
  postalCode: z.string().optional(),
})

export type ProfileSettingsRequest = z.infer<typeof profileSettingsRequestSchema>

// Security settings request
export const securitySettingsRequestSchema = z
  .object({
    currentPassword: z.string().min(1, "Current password is required"),
    newPassword: z
      .string()
      .min(6, "New password must be at least 6 characters")
      .regex(/[A-Z]/, "Password must contain at least one uppercase letter")
      .regex(/[a-z]/, "Password must contain at least one lowercase letter")
      .regex(/[0-9]/, "Password must contain at least one digit")
      .regex(/[@$!%*?&]/, "Password must contain at least one special character"),
    confirmPassword: z.string().min(1, "Please confirm your password"),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  })

export type SecuritySettingsRequest = z.infer<typeof securitySettingsRequestSchema>

// Business settings request
export const businessSettingsRequestSchema = z.object({
  companyName: z.string().optional(),
  industry: z.string().optional(),
  businessType: z.string().optional(),
  fiscalYearStart: z.string().optional(),
  currency: z.string().optional(),
  timezone: z.string().optional(),
})

export type BusinessSettingsRequest = z.infer<typeof businessSettingsRequestSchema>

// Notification settings request
export const notificationSettingsRequestSchema = z.object({
  emailNotifications: z.boolean().optional(),
  lowStockAlerts: z.boolean().optional(),
  orderNotifications: z.boolean().optional(),
  reportNotifications: z.boolean().optional(),
})

export type NotificationSettingsRequest = z.infer<typeof notificationSettingsRequestSchema>
