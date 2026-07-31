import { z } from "zod"
import { roleNameEnum } from "./request"

// ========================================
// AUTH RESPONSE SCHEMAS
// ========================================

// User schema
export const userSchema = z.object({
  id: z.number(),
  username: z.string(),
  email: z.email(),
  roleName: roleNameEnum,
  isActive: z.boolean(),
  lastLogin: z.iso.datetime({local: true}).nullable().optional(),
  createdAt: z.iso.datetime({local: true}),
  updatedAt: z.iso.datetime({local: true}).optional(),
})

export type User = z.infer<typeof userSchema>

// Auth response (tokens are in HttpOnly cookies)
export const authResponseSchema = z.object({
  tokenType: z.string(),
  expiresIn: z.number(),
  user: userSchema,
})

export type AuthResponse = z.infer<typeof authResponseSchema>

// Boolean response (for /validate endpoint)
export const booleanResponseSchema = z.boolean()

// Logout response
export const logoutResponseSchema = z.object({
  success: z.boolean(),
  message: z.string(),
  data: z.null(),
})

// User update request
export const userUpdateRequestSchema = z.object({
  email: z.email().optional(),
  roleName: roleNameEnum.optional(),
})

export type UserUpdateRequest = z.infer<typeof userUpdateRequestSchema>

// User list item (for paginated responses)
export const userListItemSchema = userSchema

// Role schema
export const roleSchema = z.object({
  id: z.number(),
  name: z.string(),
  description: z.string().optional(),
})

export type Role = z.infer<typeof roleSchema>

// User stats
export const userStatsSchema = z.object({
  totalUsers: z.number(),
  activeUsers: z.number(),
  inactiveUsers: z.number(),
  adminCount: z.number(),
  managerCount: z.number(),
  warehouseStaffCount: z.number(),
  viewerCount: z.number(),
})

export type UserStats = z.infer<typeof userStatsSchema>
