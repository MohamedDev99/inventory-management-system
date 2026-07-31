// ========================================
// AUTH TYPES
// ========================================

export interface User {
  id: number
  username: string
  email: string
  roleName: "ADMIN" | "MANAGER" | "WAREHOUSE_STAFF" | "VIEWER"
  isActive: boolean
  lastLogin?: string | null
  createdAt: string
  updatedAt?: string
}

export interface Role {
  id: number
  name: string
  description?: string
}

// AuthResponse - tokens are in HttpOnly cookies, not in response body
export interface AuthResponse {
  tokenType: string
  expiresIn: number
  user: User
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  roleName: "ADMIN" | "MANAGER" | "WAREHOUSE_STAFF" | "VIEWER"
}
