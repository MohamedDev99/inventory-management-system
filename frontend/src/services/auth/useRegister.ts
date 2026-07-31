import { useMutation } from "@tanstack/react-query"
import { register as registerApi } from "./api"
import type { RegisterRequest } from "@/types"
import toast from "react-hot-toast"
import { useAuthStore } from "@/store/authStore"
import { useNavigate } from "react-router-dom"
import { APP_ROUTES } from "@/constants"

export function useRegister() {
  const setAuth = useAuthStore((state) => state.setAuth)
  const navigate = useNavigate()

  return useMutation({
    mutationFn: (data: RegisterRequest) => registerApi(data),
    onSuccess: (data) => {
      setAuth(data.data.user)
      toast.success("Account created successfully!")
      navigate(APP_ROUTES.DASHBOARD)
    },
    onError: () => {
      toast.error("Registration failed. Please try again.")
    },
  })
}
