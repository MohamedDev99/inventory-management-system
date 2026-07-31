import { z } from "zod"

// ========================================
// AUTH REQUEST SCHEMAS
// ========================================

// Login request
export const loginRequestSchema = z.object({
  username: z.string().min(1, "Username is required"),
  password: z.string().min(1, "Password is required"),
})

export type LoginRequest = z.infer<typeof loginRequestSchema>

// Register request - password requirements: min 8 chars, must contain uppercase, lowercase, digit, special char
export const registerRequestSchema = z
  .object({
    username: z.string().min(3, "Username must be at least 3 characters"),
    email: z.string().email("Invalid email address"),
    password: z
      .string()
      .min(8, "Password must be at least 8 characters")
      .regex(/[A-Z]/, "Password must contain at least one uppercase letter")
      .regex(/[a-z]/, "Password must contain at least one lowercase letter")
      .regex(/[0-9]/, "Password must contain at least one digit")
      .regex(/[@$!%*?&]/, "Password must contain at least one special character (@$!%*?&)"),
    confirmPassword: z.string(),
    roleName: z.enum(["ADMIN", "MANAGER", "WAREHOUSE_STAFF", "VIEWER"]),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  })

export type RegisterRequest = z.infer<typeof registerRequestSchema>

// Change password request
export const changePasswordRequestSchema = z
  .object({
    currentPassword: z.string().min(1, "Current password is required"),
    newPassword: z
      .string()
      .min(8, "Password must be at least 8 characters")
      .regex(/[A-Z]/, "Password must contain at least one uppercase letter")
      .regex(/[a-z]/, "Password must contain at least one lowercase letter")
      .regex(/[0-9]/, "Password must contain at least one digit")
      .regex(/[@$!%*?&]/, "Password must contain at least one special character (@$!%*?&)"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  })

export type ChangePasswordRequest = z.infer<typeof changePasswordRequestSchema>

// Forgot password request
export const forgotPasswordRequestSchema = z.object({
  email: z.string().email("Invalid email address"),
})

export type ForgotPasswordRequest = z.infer<typeof forgotPasswordRequestSchema>

// Reset password request
export const resetPasswordRequestSchema = z.object({
  token: z.string().min(1, "Token is required"),
  newPassword: z
    .string()
    .min(8, "Password must be at least 8 characters")
    .regex(/[A-Z]/, "Password must contain at least one uppercase letter")
    .regex(/[a-z]/, "Password must contain at least one lowercase letter")
    .regex(/[0-9]/, "Password must contain at least one digit")
    .regex(/[@$!%*?&]/, "Password must contain at least one special character (@$!%*?&)"),
  confirmPassword: z.string(),
})

export type ResetPasswordRequest = z.infer<typeof resetPasswordRequestSchema>

// Admin reset password request
export const adminResetPasswordRequestSchema = z.object({
  newPassword: z
    .string()
    .min(8, "Password must be at least 8 characters")
    .regex(/[A-Z]/, "Password must contain at least one uppercase letter")
    .regex(/[a-z]/, "Password must contain at least one lowercase letter")
    .regex(/[0-9]/, "Password must contain at least one digit")
    .regex(/[@$!%*?&]/, "Password must contain at least one special character (@$!%*?&)"),
})

export type AdminResetPasswordRequest = z.infer<typeof adminResetPasswordRequestSchema>

// Onboarding request
export const onboardingRequestSchema = z.object({
  businessName: z.string().min(1, "Business name is required"),
  industry: z.string().min(1, "Industry is required"),
  domain: z.string().min(1, "Domain is required"),
  productType: z.string().min(1, "Product type is required"),
  businessType: z.string().min(1, "Business type is required"),
  skuSize: z.string().min(1, "SKU size is required"),
})

export type OnboardingRequest = z.infer<typeof onboardingRequestSchema>

// Role enum for reference
export const roleNameEnum = z.enum(["ADMIN", "MANAGER", "WAREHOUSE_STAFF", "VIEWER"])

export type RoleName = z.infer<typeof roleNameEnum>
