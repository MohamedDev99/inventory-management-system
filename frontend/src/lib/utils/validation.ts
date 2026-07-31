import { z, ZodError } from "zod"
import type { AxiosResponse } from "axios"
import { apiResponseSchema } from "@/lib/schemas/common/api"

// ========================================
// ZOD RESPONSE VALIDATION UTILITIES
// ========================================

/**
 * Validates API response data against a Zod schema using safeParse
 * @param schema - The Zod schema to validate against
 * @param data - The data to validate
 * @param options - Validation options
 * @returns The validated data if successful
 * @throws ZodError if validation fails
 */
export function validateResponse<T extends z.ZodTypeAny>(
  schema: T,
  data: unknown,
  options?: {
    /** Custom error message prefix */
    prefix?: string
    /** Whether to log validation errors to console */
    logErrors?: boolean
  }
): z.infer<T> {
  const { prefix = "Response validation", logErrors = true } = options || {}

  const result = schema.safeParse(data)

  if (!result.success) {
    if (logErrors) {
      console.error(`${prefix} failed:`, result.error.issues)
    }
    throw new ZodError(result.error.issues)
  }

  return result.data
}

/**
 * Validates an Axios response data against a Zod schema
 * @param response - The Axios response
 * @param schema - The Zod schema to validate against
 * @param options - Validation options
 * @returns The validated data
 */
export function validateAxiosResponse<T extends z.ZodTypeAny>(
  response: AxiosResponse,
  schema: T,
  options?: {
    prefix?: string
    logErrors?: boolean
  }
): z.infer<T> {
  return validateResponse(schema, response.data, options)
}

/**
 * Creates a typed wrapper for API functions that validates responses
 * @param apiCall - The API call function that returns Axios response
 * @param schema - The Zod schema to validate response against
 * @param options - Validation options
 * @returns The validated data
 */
export async function withValidation<T extends z.ZodTypeAny>(
  apiCall: () => Promise<AxiosResponse>,
  schema: T,
  options?: {
    prefix?: string
    logErrors?: boolean
  }
): Promise<z.infer<T>> {
  const response = await apiCall()
  return validateAxiosResponse(response, schema, options)
}

// ========================================
// API RESPONSE VALIDATORS
// ========================================

// Generic API response wrapper validator
export function createApiResponseValidator<T extends z.ZodTypeAny>(dataSchema: T) {
  return (response: AxiosResponse) => {
    return validateAxiosResponse(response, apiResponseSchema(dataSchema), {
      prefix: "API Response",
    })
  }
}

// Import the common API response schema
export { apiResponseSchema } from "@/lib/schemas/common/api"
