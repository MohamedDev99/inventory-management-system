import { z } from "zod"

// ========================================
// CATEGORY RESPONSE SCHEMAS
// ========================================

// Parent category reference
export const parentCategorySchema = z.object({
  id: z.number(),
  name: z.string(),
  code: z.string(),
  fullPath: z.string().optional(),
})

export type ParentCategory = z.infer<typeof parentCategorySchema>

// Category response
export const categorySchema = z.object({
  id: z.number(),
  name: z.string(),
  code: z.string(),
  description: z.string().optional(),
  parentCategory: parentCategorySchema.nullable().optional(),
  level: z.number(),
  fullPath: z.string().optional(),
  productCount: z.number().optional(),
  childCategoryCount: z.number().optional(),
  isRootCategory: z.boolean().optional(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime().optional(),
})

export type Category = z.infer<typeof categorySchema>

// Category tree node (for hierarchical responses)
export const categoryTreeNodeSchema: z.ZodType<{
  id: number
  name: string
  code: string
  description?: string
  level: number
  productCount: number
  children: z.infer<typeof categoryTreeNodeSchema>[]
}> = z.lazy(() =>
  z.object({
    id: z.number(),
    name: z.string(),
    code: z.string(),
    description: z.string().optional(),
    level: z.number(),
    productCount: z.number(),
    children: z.array(categoryTreeNodeSchema),
  })
)

export type CategoryTreeNode = z.infer<typeof categoryTreeNodeSchema>

// Category list item (for dropdowns)
export const categoryListItemSchema = z.object({
  id: z.number(),
  name: z.string(),
  code: z.string(),
  fullPath: z.string().optional(),
  level: z.number(),
  productCount: z.number().optional(),
  hasChildren: z.boolean().optional(),
})

export type CategoryListItem = z.infer<typeof categoryListItemSchema>

// Paginated category list
export const categoryListResponseSchema = z.object({
  content: z.array(categorySchema),
  totalElements: z.number(),
  totalPages: z.number(),
  size: z.number(),
  number: z.number(),
})

export type CategoryListResponse = z.infer<typeof categoryListResponseSchema>
