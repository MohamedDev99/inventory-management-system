import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { userSchema, userStatsSchema } from "@/lib/schemas/auth"
import { warehouseSchema } from "@/lib/schemas/warehouse"
import type { PageParams } from "@/types"

export interface UserParams extends PageParams {
  isActive?: boolean
}

// Request types
export type RoleName = "ADMIN" | "MANAGER" | "WAREHOUSE_STAFF" | "VIEWER"

export type CreateUserRequest = {
  username: string
  email: string
  password: string
  roleName: RoleName
}

export type UpdateUserRequest = {
  email?: string
  roleName?: RoleName
}

export type AssignWarehousesRequest = { warehouseIds: number[] }

// GET /api/users - Paginated list of active users
export async function getUsers(params: UserParams = {}) {
  const response = await api.get("/users", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(userSchema)),
    response.data,
    { prefix: "Get Users Response" }
  )
  return validated
}

// GET /api/users/search?q={term} - Search by username or email
export async function searchUsers(term: string, params: PageParams = {}) {
  const response = await api.get("/users/search", { params: { q: term, ...params } })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(userSchema)),
    response.data,
    { prefix: "Search Users Response" }
  )
  return validated
}

// GET /api/users/stats - User statistics
export async function getUserStats() {
  const response = await api.get("/users/stats")
  const validated = validateResponse(
    apiResponseSchema(userStatsSchema),
    response.data,
    { prefix: "Get User Stats Response" }
  )
  return validated
}

// GET /api/users/{id} - Get user by ID
export async function getUser(id: number) {
  const response = await api.get(`/users/${id}`)
  const validated = validateResponse(
    apiResponseSchema(userSchema),
    response.data,
    { prefix: "Get User Response" }
  )
  return validated
}

// POST /api/users - Admin creates user
export async function createUser(data: {
  username: string
  email: string
  password: string
  roleName: "ADMIN" | "MANAGER" | "WAREHOUSE_STAFF" | "VIEWER"
}) {
  const response = await api.post("/users", data)
  const validated = validateResponse(
    apiResponseSchema(userSchema),
    response.data,
    { prefix: "Create User Response" }
  )
  return validated
}

// PUT /api/users/{id} - Update user
export async function updateUser(id: number, data: { email?: string; roleName?: "ADMIN" | "MANAGER" | "WAREHOUSE_STAFF" | "VIEWER" }) {
  const response = await api.put(`/users/${id}`, data)
  const validated = validateResponse(
    apiResponseSchema(userSchema),
    response.data,
    { prefix: "Update User Response" }
  )
  return validated
}

// POST /api/users/{id}/change-password - Change own password
export async function changeUserPassword(id: number, data: { currentPassword: string; newPassword: string; confirmPassword: string }): Promise<void> {
  await api.post(`/users/${id}/change-password`, data)
}

// DELETE /api/users/{id} - Soft-deactivate user
export async function deleteUser(id: number): Promise<void> {
  await api.delete(`/users/${id}`)
}

// PATCH /api/users/{id}/activate - Reactivate user
export async function activateUser(id: number) {
  const response = await api.patch(`/users/${id}/activate`)
  const validated = validateResponse(
    apiResponseSchema(userSchema),
    response.data,
    { prefix: "Activate User Response" }
  )
  return validated
}

// PATCH /api/users/{id}/unlock - Unlock locked account
export async function unlockUser(id: number) {
  const response = await api.patch(`/users/${id}/unlock`)
  const validated = validateResponse(
    apiResponseSchema(userSchema),
    response.data,
    { prefix: "Unlock User Response" }
  )
  return validated
}

// PUT /api/users/{id}/warehouses - Assign warehouses to user
export async function assignUserWarehouses(id: number, data: { warehouseIds: number[] }) {
  const response = await api.put(`/users/${id}/warehouses`, data)
  const validated = validateResponse(
    apiResponseSchema(userSchema),
    response.data,
    { prefix: "Assign User Warehouses Response" }
  )
  return validated
}

// GET /api/users/{id}/warehouses - Get user's assigned warehouses
export async function getUserWarehouses(id: number) {
  const response = await api.get(`/users/${id}/warehouses`)
  const validated = validateResponse(
    apiResponseSchema(z.array(warehouseSchema)),
    response.data,
    { prefix: "Get User Warehouses Response" }
  )
  return validated
}

// PATCH /api/users/{id} - Partial update
export async function patchUser(id: number, data: { email?: string; roleName?: "ADMIN" | "MANAGER" | "WAREHOUSE_STAFF" | "VIEWER" }) {
  const response = await api.patch(`/users/${id}`, data)
  const validated = validateResponse(
    apiResponseSchema(userSchema),
    response.data,
    { prefix: "Patch User Response" }
  )
  return validated
}
