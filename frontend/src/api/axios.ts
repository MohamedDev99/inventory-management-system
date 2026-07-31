import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios"
import toast from "react-hot-toast"

// Add this at the top of the file, outside the axios instance
declare module 'axios' {
  interface InternalAxiosRequestConfig {
    _retry?: boolean
  }
}

const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api"

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
    'Bypass-Tunnel-Reminder': 'true'
  },
  withCredentials: true, // Required for HttpOnly cookies
})

// Request interceptor - HttpOnly cookies are automatically sent by browser
// No manual token handling needed
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Cookies are automatically sent with requests when withCredentials: true
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config

    // Handle 401 Unauthorized - try to refresh token
    // With HttpOnly cookies, the browser automatically sends refresh_token cookie
    if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
      originalRequest._retry = true   // ← prevents infinite retry loop
      // Skip retrying auth endpoints to avoid infinite loops
      const isAuthEndpoint = originalRequest.url?.includes("/auth/")

      if (!isAuthEndpoint) {
        try {
          // Attempt token refresh - backend reads refresh_token from HttpOnly cookie
          await api.post("/auth/refresh")

          // Retry original request after successful refresh
          return api(originalRequest)
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        } catch (refreshError) {
          // Refresh failed - clear any local state and redirect to login
          window.location.href = "/login"
          toast.error("Session expired. Please log in again.")
        }
      }
    }

    // Handle other errors
    const errorData = error.response?.data as Record<string, unknown>
    const errorMessage =
      (errorData?.message as string) ||
      (errorData?.error as string) ||
      "An error occurred"

    // Don't show toast for 401 (already handled above) or cancellation
    if (error.response?.status !== 401 && !error.message?.includes("canceled")) {
      toast.error(errorMessage)
    }

    return Promise.reject(error)
  }
)

export default api
