import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, booleanResponseSchema } from "@/lib/schemas/common/api"
import { authResponseSchema, userSchema } from "@/lib/schemas/auth"
import type { LoginRequest, RegisterRequest } from "@/types"

export async function login(credentials: LoginRequest) {
  const response = await api.post("/auth/login", credentials)
  const validated = validateResponse(
    apiResponseSchema(authResponseSchema),
    response.data,
    { prefix: "Login Response" }
  )
  return validated
}

export async function register(data: RegisterRequest) {
  const response = await api.post("/auth/register", data)
  const validated = validateResponse(
    apiResponseSchema(authResponseSchema),
    response.data,
    { prefix: "Register Response" }
  )
  return validated
}

export async function logout(): Promise<void> {
  await api.post("/auth/logout")
}

// Refresh token - HttpOnly cookie is automatically sent by browser
export async function refreshToken() {
  const response = await api.post("/auth/refresh")
  const validated = validateResponse(
    apiResponseSchema(authResponseSchema),
    response.data,
    { prefix: "Refresh Token Response" }
  )
  return validated
}

// Get current authenticated user
export async function getCurrentUser() {
  const response = await api.get("/auth/me")
  const validated = validateResponse(
    apiResponseSchema(userSchema),
    response.data,
    { prefix: "Get Current User Response" }
  )
  return validated
}

export async function changePassword(data: {
  currentPassword: string
  newPassword: string
}): Promise<void> {
  await api.post("/auth/change-password", data)
}

export async function forgotPassword(email: string): Promise<void> {
  await api.post("/auth/forgot-password", { email })
}

export async function resetPassword(data: {
  token: string
  newPassword: string
}): Promise<void> {
  await api.post("/auth/reset-password", data)
}

export async function onboarding(data: {
  businessName: string
  industry: string
  domain: string
  productType: string
  businessType: string
  skuSize: string
}): Promise<void> {
  await api.post("/auth/onboarding", data)
}

// Validate token
export async function validateToken() {
  const response = await api.get("/auth/validate")
  const validated = validateResponse(
    apiResponseSchema(booleanResponseSchema),
    response.data,
    { prefix: "Validate Token Response" }
  )
  return validated
}
