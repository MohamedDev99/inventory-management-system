import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { departmentSchema } from "@/lib/schemas/department"
import { createDepartmentRequestSchema, updateDepartmentRequestSchema } from "@/lib/schemas/department/request"
import type { PageParams } from "@/types"

// GET /api/departments - Paginated list
export async function getDepartments(params: PageParams = {}) {
  const response = await api.get("/departments", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(departmentSchema)),
    response.data,
    { prefix: "Get Departments Response" }
  )
  return validated
}

// GET /api/departments/{id} - Department detail
export async function getDepartment(id: number) {
  const response = await api.get(`/departments/${id}`)
  const validated = validateResponse(
    apiResponseSchema(departmentSchema),
    response.data,
    { prefix: "Get Department Response" }
  )
  return validated
}

// POST /api/departments - Create department
export async function createDepartment(data: Parameters<typeof createDepartmentRequestSchema.parse>[0]) {
  const validatedData = createDepartmentRequestSchema.parse(data)
  const response = await api.post("/departments", validatedData)
  const validated = validateResponse(
    apiResponseSchema(departmentSchema),
    response.data,
    { prefix: "Create Department Response" }
  )
  return validated
}

// PUT /api/departments/{id} - Update department
export async function updateDepartment(id: number, data: Parameters<typeof updateDepartmentRequestSchema.parse>[0]) {
  const validatedData = updateDepartmentRequestSchema.parse(data)
  const response = await api.put(`/departments/${id}`, validatedData)
  const validated = validateResponse(
    apiResponseSchema(departmentSchema),
    response.data,
    { prefix: "Update Department Response" }
  )
  return validated
}

// DELETE /api/departments/{id} - Delete department
export async function deleteDepartment(id: number): Promise<void> {
  await api.delete(`/departments/${id}`)
}
