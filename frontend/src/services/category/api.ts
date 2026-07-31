import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { categorySchema, categoryTreeNodeSchema, categoryListItemSchema } from "@/lib/schemas/category"
import { createCategoryRequestSchema, updateCategoryRequestSchema } from "@/lib/schemas/category/request"
import type { PageParams } from "@/types"

// GET /api/categories - Paginated list
export async function getCategories(params: PageParams = {}) {
  const response = await api.get("/categories", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(categorySchema)),
    response.data,
    { prefix: "Get Categories Response" }
  )
  return validated
}

// GET /api/categories/{id} - Category detail
export async function getCategory(id: number) {
  const response = await api.get(`/categories/${id}`)
  const validated = validateResponse(
    apiResponseSchema(categorySchema),
    response.data,
    { prefix: "Get Category Response" }
  )
  return validated
}

// GET /api/categories/code/{code} - Lookup by code
export async function getCategoryByCode(code: string) {
  const response = await api.get(`/categories/code/${code}`)
  const validated = validateResponse(
    apiResponseSchema(categorySchema),
    response.data,
    { prefix: "Get Category by Code Response" }
  )
  return validated
}

// GET /api/categories/tree - Full hierarchy as nested tree
export async function getCategoryTree() {
  const response = await api.get("/categories/tree")
  const validated = validateResponse(
    apiResponseSchema(z.array(categoryTreeNodeSchema)),
    response.data,
    { prefix: "Get Category Tree Response" }
  )
  return validated
}

// GET /api/categories/{id}/subtree - Subtree starting from a category
export async function getCategorySubtree(id: number) {
  const response = await api.get(`/categories/${id}/subtree`)
  const validated = validateResponse(
    apiResponseSchema(categoryTreeNodeSchema),
    response.data,
    { prefix: "Get Category Subtree Response" }
  )
  return validated
}

// GET /api/categories/roots - All top-level categories
export async function getRootCategories(params: PageParams = {}) {
  const response = await api.get("/categories/roots", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(categorySchema)),
    response.data,
    { prefix: "Get Root Categories Response" }
  )
  return validated
}

// GET /api/categories/{parentId}/children - Paginated children of parent
export async function getCategoryChildren(parentId: number, params: PageParams = {}) {
  const response = await api.get(`/categories/${parentId}/children`, { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(categorySchema)),
    response.data,
    { prefix: "Get Category Children Response" }
  )
  return validated
}

// GET /api/categories/list - Flat ordered list for dropdowns
export async function getCategoryList() {
  const response = await api.get("/categories/list")
  const validated = validateResponse(
    apiResponseSchema(z.array(categoryListItemSchema)),
    response.data,
    { prefix: "Get Category List Response" }
  )
  return validated
}

// GET /api/categories/{id}/products - Total product count within category and subcategories
export async function getCategoryProductCount(id: number) {
  const response = await api.get(`/categories/${id}/products`)
  const validated = validateResponse(
    apiResponseSchema(z => z.number()),
    response.data,
    { prefix: "Get Category Product Count Response" }
  )
  return validated
}

// GET /api/categories/empty - Categories with no products
export async function getEmptyCategories() {
  const response = await api.get("/categories/empty")
  const validated = validateResponse(
    apiResponseSchema(z.array(categorySchema)),
    response.data,
    { prefix: "Get Empty Categories Response" }
  )
  return validated
}

// POST /api/categories - Create new category
export async function createCategory(data: Parameters<typeof createCategoryRequestSchema.parse>[0]) {
  const validatedData = createCategoryRequestSchema.parse(data)
  const response = await api.post("/categories", validatedData)
  const validated = validateResponse(
    apiResponseSchema(categorySchema),
    response.data,
    { prefix: "Create Category Response" }
  )
  return validated
}

// PUT /api/categories/{id} - Full category update
export async function updateCategory(id: number, data: Parameters<typeof updateCategoryRequestSchema.parse>[0]) {
  const validatedData = updateCategoryRequestSchema.parse(data)
  const response = await api.put(`/categories/${id}`, validatedData)
  const validated = validateResponse(
    apiResponseSchema(categorySchema),
    response.data,
    { prefix: "Update Category Response" }
  )
  return validated
}

// PATCH /api/categories/{id} - Partial update
export async function patchCategory(id: number, data: Parameters<typeof updateCategoryRequestSchema.parse>[0]) {
  const validatedData = updateCategoryRequestSchema.parse(data)
  const response = await api.patch(`/categories/${id}`, validatedData)
  const validated = validateResponse(
    apiResponseSchema(categorySchema),
    response.data,
    { prefix: "Patch Category Response" }
  )
  return validated
}

// DELETE /api/categories/{id} - Hard delete
export async function deleteCategory(id: number): Promise<void> {
  await api.delete(`/categories/${id}`)
}
