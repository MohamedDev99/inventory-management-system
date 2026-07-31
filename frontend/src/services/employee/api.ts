import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { employeeSchema } from "@/lib/schemas/employee"
import { createEmployeeRequestSchema, updateEmployeeRequestSchema } from "@/lib/schemas/employee/request"
import type { PageParams } from "@/types"

// GET /api/employees - Paginated list
export async function getEmployees(params: PageParams = {}) {
  const response = await api.get("/employees", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(employeeSchema)),
    response.data,
    { prefix: "Get Employees Response" }
  )
  return validated
}

// GET /api/employees/{id} - Employee detail
export async function getEmployee(id: number) {
  const response = await api.get(`/employees/${id}`)
  const validated = validateResponse(
    apiResponseSchema(employeeSchema),
    response.data,
    { prefix: "Get Employee Response" }
  )
  return validated
}

// POST /api/employees - Create employee
export async function createEmployee(data: Parameters<typeof createEmployeeRequestSchema.parse>[0]) {
  const validatedData = createEmployeeRequestSchema.parse(data)
  const response = await api.post("/employees", validatedData)
  const validated = validateResponse(
    apiResponseSchema(employeeSchema),
    response.data,
    { prefix: "Create Employee Response" }
  )
  return validated
}

// PUT /api/employees/{id} - Update employee
export async function updateEmployee(id: number, data: Parameters<typeof updateEmployeeRequestSchema.parse>[0]) {
  const validatedData = updateEmployeeRequestSchema.parse(data)
  const response = await api.put(`/employees/${id}`, validatedData)
  const validated = validateResponse(
    apiResponseSchema(employeeSchema),
    response.data,
    { prefix: "Update Employee Response" }
  )
  return validated
}

// DELETE /api/employees/{id} - Delete employee
export async function deleteEmployee(id: number): Promise<void> {
  await api.delete(`/employees/${id}`)
}
