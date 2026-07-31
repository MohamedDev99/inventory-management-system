// ========================================
// API RESPONSE TYPES
// ========================================

// New API Response Wrapper (ApiResponseWpp<T>)
export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
  timestamp: string
}

// Error response types
export interface ApiError {
  timestamp: string
  status: number
  error: string
  message: string
  path?: string
}

export interface ValidationErrorResponse extends ApiError {
  fieldErrors?: Record<string, string>
}
