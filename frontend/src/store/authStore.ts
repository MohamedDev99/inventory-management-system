import { create } from "zustand"
import { persist } from "zustand/middleware"
import type { User } from "@/types"

interface AuthState {
  user: User | null
  isAuthenticated: boolean
  setAuth: (user: User) => void
  logout: () => void
  updateUser: (user: Partial<User>) => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      isAuthenticated: false,
      setAuth: (user) => {
        // Tokens are handled via HttpOnly cookies - no localStorage needed
        set({ user, isAuthenticated: true })
      },
      logout: () => {
        // Tokens are cleared via HttpOnly cookies - just clear local state
        set({ user: null, isAuthenticated: false })
      },
      updateUser: (userData) =>
        set((state) => ({
          user: state.user ? { ...state.user, ...userData } : null,
        })),
    }),
    {
      name: "auth-storage",
      partialize: (state) => ({
        user: state.user,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
)
