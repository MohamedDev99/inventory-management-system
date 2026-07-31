import { z } from "zod"

// ========================================
// API RESPONSE SCHEMA
// ========================================

export const apiResponseSchema = <T extends z.ZodTypeAny>(dataSchema: T) =>
  z.object({
    success: z.boolean(),
    message: z.string(),
    data: dataSchema,
    timestamp: z.iso.datetime({ message: "Invalid timestamp", local: true }),
  })

export type ApiResponseSchema<T> = z.infer<ReturnType<typeof apiResponseSchema<T>>>

// ========================================
// PAGINATION SCHEMA
// ========================================

export const pageParamsSchema = z.object({
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
  sort: z.string().optional(),
  search: z.string().optional(),
  isActive: z.boolean().optional(),
})

export type PageParamsSchema = z.infer<typeof pageParamsSchema>

// Page metadata schema
export const pageMetadataSchema = z.object({
  page: z.number(),
  size: z.number(),
  totalElements: z.number(),
  totalPages: z.number(),
  first: z.boolean(),
  last: z.boolean(),
  numberOfElements: z.number().optional(),
  empty: z.boolean().optional(),
})

export type PageMetadata = z.infer<typeof pageMetadataSchema>

// Generic paginated response schema
export const paginatedResponseSchema = <T extends z.ZodTypeAny>(itemSchema: T) =>
  z.object({
    content: z.array(itemSchema),
    page: z.number(),
    size: z.number(),
    totalElements: z.number(),
    totalPages: z.number(),
    first: z.boolean(),
    last: z.boolean(),
  })

export type PaginatedResponse<T> = z.infer<ReturnType<typeof paginatedResponseSchema<T>>>

// ========================================
// BOOLEAN RESPONSE SCHEMA
// ========================================

export const booleanResponseSchema = z.boolean()

export type BooleanResponse = z.infer<typeof booleanResponseSchema>

// ========================================
// NULL RESPONSE SCHEMA
// ========================================

export const nullResponseSchema = z.null()

export type NullResponse = z.infer<typeof nullResponseSchema>
