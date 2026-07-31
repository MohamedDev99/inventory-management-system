import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { supplierSchema, supplierPerformanceSchema } from "@/lib/schemas/supplier"
import { purchaseOrderSummarySchema } from "@/lib/schemas/purchase-order"
import { createSupplierRequestSchema, updateSupplierRequestSchema } from "@/lib/schemas/supplier/request"
import type { PageParams } from "@/types"

// GET /api/suppliers - Paginated list
export async function getSuppliers(params: PageParams = {}) {
  const response = await api.get("/suppliers", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(supplierSchema)),
    response.data,
    { prefix: "Get Suppliers Response" }
  )
  return validated
}

// GET /api/suppliers/{id} - Full supplier detail
export async function getSupplier(id: number) {
  const response = await api.get(`/suppliers/${id}`)
  const validated = validateResponse(
    apiResponseSchema(supplierSchema),
    response.data,
    { prefix: "Get Supplier Response" }
  )
  return validated
}

// GET /api/suppliers/code/{code} - Lookup by code
export async function getSupplierByCode(code: string) {
  const response = await api.get(`/suppliers/code/${code}`)
  const validated = validateResponse(
    apiResponseSchema(supplierSchema),
    response.data,
    { prefix: "Get Supplier by Code Response" }
  )
  return validated
}

// GET /api/suppliers/top-rated - Suppliers sorted by rating
export async function getTopRatedSuppliers(params: PageParams = {}) {
  const response = await api.get("/suppliers/top-rated", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(supplierSchema)),
    response.data,
    { prefix: "Get Top Rated Suppliers Response" }
  )
  return validated
}

// GET /api/suppliers/search?term={term} - Search by name, code, or email
export async function searchSuppliers(term: string, params: PageParams = {}) {
  const response = await api.get("/suppliers/search", { params: { term, ...params } })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(supplierSchema)),
    response.data,
    { prefix: "Search Suppliers Response" }
  )
  return validated
}

// GET /api/suppliers/count/active - Total active supplier count
export async function getActiveSupplierCount() {
  const response = await api.get("/suppliers/count/active")
  const validated = validateResponse(
    apiResponseSchema(z => z.number()),
    response.data,
    { prefix: "Get Active Supplier Count Response" }
  )
  return validated
}

// GET /api/suppliers/{id}/orders - Purchase order history
export async function getSupplierOrders(supplierId: number, params: PageParams = {}) {
  const response = await api.get(`/suppliers/${supplierId}/orders`, { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(purchaseOrderSummarySchema)),
    response.data,
    { prefix: "Get Supplier Orders Response" }
  )
  return validated
}

// GET /api/suppliers/{id}/performance - Performance metrics
export async function getSupplierPerformance(supplierId: number) {
  const response = await api.get(`/suppliers/${supplierId}/performance`)
  const validated = validateResponse(
    apiResponseSchema(supplierPerformanceSchema),
    response.data,
    { prefix: "Get Supplier Performance Response" }
  )
  return validated
}

// POST /api/suppliers - Create new supplier
export async function createSupplier(data: Parameters<typeof createSupplierRequestSchema.parse>[0]) {
  const validatedData = createSupplierRequestSchema.parse(data)
  const response = await api.post("/suppliers", validatedData)
  const validated = validateResponse(
    apiResponseSchema(supplierSchema),
    response.data,
    { prefix: "Create Supplier Response" }
  )
  return validated
}

// PUT /api/suppliers/{id} - Full update
export async function updateSupplier(id: number, data: Parameters<typeof updateSupplierRequestSchema.parse>[0]) {
  const validatedData = updateSupplierRequestSchema.parse(data)
  const response = await api.put(`/suppliers/${id}`, validatedData)
  const validated = validateResponse(
    apiResponseSchema(supplierSchema),
    response.data,
    { prefix: "Update Supplier Response" }
  )
  return validated
}

// PATCH /api/suppliers/{id} - Partial update
export async function patchSupplier(id: number, data: Parameters<typeof updateSupplierRequestSchema.parse>[0]) {
  const validatedData = updateSupplierRequestSchema.parse(data)
  const response = await api.patch(`/suppliers/${id}`, validatedData)
  const validated = validateResponse(
    apiResponseSchema(supplierSchema),
    response.data,
    { prefix: "Patch Supplier Response" }
  )
  return validated
}

// DELETE /api/suppliers/{id} - Soft-delete (isActive = false)
export async function deleteSupplier(id: number): Promise<void> {
  await api.delete(`/suppliers/${id}`)
}
