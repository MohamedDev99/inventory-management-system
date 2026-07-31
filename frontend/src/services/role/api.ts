import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema } from "@/lib/schemas/common/api"
import { roleSchema } from "@/lib/schemas/auth"

// Role schema
export type Role = z.infer<typeof roleSchema>

// Request types
export type CreateRoleRequest = { name: string; description?: string }
export type UpdateRoleRequest = { name: string; description?: string }

// GET /api/roles - List all roles
export async function getRoles() {
  const response = await api.get("/roles")
  const validated = validateResponse(
    apiResponseSchema(z.array(roleSchema)),
    response.data,
    { prefix: "Get Roles Response" }
  )
  return validated
}

// GET /api/roles/{id} - Role detail
export async function getRole(id: number) {
  const response = await api.get(`/roles/${id}`)
  const validated = validateResponse(
    apiResponseSchema(roleSchema),
    response.data,
    { prefix: "Get Role Response" }
  )
  return validated
}

// POST /api/roles - Create role
export async function createRole(data: { name: string; description?: string }) {
  const response = await api.post("/roles", data)
  const validated = validateResponse(
    apiResponseSchema(roleSchema),
    response.data,
    { prefix: "Create Role Response" }
  )
  return validated
}

// PUT /api/roles/{id} - Update role
export async function updateRole(id: number, data: { name: string; description?: string }) {
  const response = await api.put(`/roles/${id}`, data)
  const validated = validateResponse(
    apiResponseSchema(roleSchema),
    response.data,
    { prefix: "Update Role Response" }
  )
  return validated
}

// DELETE /api/roles/{id} - Delete role
export async function deleteRole(id: number): Promise<void> {
  await api.delete(`/roles/${id}`)
}
