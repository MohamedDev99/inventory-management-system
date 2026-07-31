import { z } from "zod"

// ========================================
// ERROR RESPONSE SCHEMAS
// ========================================

// Standard Error Response
export const errorResponseSchema = z.object({
  timestamp: z.string().datetime(),
  status: z.number(),
  error: z.string(),
  message: z.string(),
  path: z.string().optional(),
})

export type ErrorResponse = z.infer<typeof errorResponseSchema>

// Validation Error Response (400)
export const validationErrorResponseSchema = errorResponseSchema.extend({
  fieldErrors: z.record(z.string()).optional(),
})

export type ValidationErrorResponse = z.infer<typeof validationErrorResponseSchema>

// Conflict Error Response (409) - for duplicate resources
export const conflictErrorResponseSchema = errorResponseSchema.extend({
  conflictField: z.string().optional(),
  conflictValue: z.string().optional(),
})

export type ConflictErrorResponse = z.infer<typeof conflictErrorResponseSchema>

// Concurrent Modification Error (409) - optimistic locking
export const concurrentModificationErrorSchema = errorResponseSchema.extend({
  entityType: z.string().optional(),
  entityId: z.number().optional(),
  expectedVersion: z.number().optional(),
  actualVersion: z.number().optional(),
})

export type ConcurrentModificationError = z.infer<typeof concurrentModificationErrorSchema>

// Pending Approval Error (409)
export const pendingApprovalErrorSchema = errorResponseSchema.extend({
  resourceType: z.string().optional(),
  resourceId: z.number().optional(),
})

export type PendingApprovalError = z.infer<typeof pendingApprovalErrorSchema>

// Account Locked Error (423)
export const accountLockedErrorSchema = errorResponseSchema.extend({
  lockedUntil: z.string().datetime().optional(),
})

export type AccountLockedError = z.infer<typeof accountLockedErrorSchema>

// Insufficient Stock Error (400)
export const insufficientStockErrorSchema = errorResponseSchema.extend({
  productId: z.number().optional(),
  warehouseId: z.number().optional(),
  warehouseName: z.string().optional(),
  availableQuantity: z.number().optional(),
  requestedQuantity: z.number().optional(),
})

export type InsufficientStockError = z.infer<typeof insufficientStockErrorSchema>

// Generic union of all error types
export const errorResponseUnionSchema = z.union([
  errorResponseSchema,
  validationErrorResponseSchema,
  conflictErrorResponseSchema,
  concurrentModificationErrorSchema,
  pendingApprovalErrorSchema,
  accountLockedErrorSchema,
  insufficientStockErrorSchema,
])

export type ErrorResponseUnion = z.infer<typeof errorResponseUnionSchema>
