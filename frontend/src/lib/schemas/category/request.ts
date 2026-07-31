import { z } from "zod"

// ========================================
// CATEGORY REQUEST SCHEMAS
// ========================================

// Create category request
export const createCategoryRequestSchema = z.object({
  name: z.string().min(1, "Category name is required"),
  code: z.string().min(1, "Category code is required"),
  description: z.string().optional(),
  parentCategoryId: z.number().optional(),
})

export type CreateCategoryRequest = z.infer<typeof createCategoryRequestSchema>

// Update category request
export const updateCategoryRequestSchema = z.object({
  name: z.string().optional(),
  description: z.string().optional(),
  parentCategoryId: z.number().optional(),
}).refine(
  (data) => Object.keys(data).length > 0,
  { message: "At least one field is required" }
)

export type UpdateCategoryRequest = z.infer<typeof updateCategoryRequestSchema>

// Category filter params
export const categoryFilterParamsSchema = z.object({
  search: z.string().optional(),
  parentId: z.number().optional(),
  level: z.number().optional(),
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
  sort: z.string().optional(),
})

export type CategoryFilterParams = z.infer<typeof categoryFilterParamsSchema>
