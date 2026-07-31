import { useMutation } from "@tanstack/react-query"
import { login as loginApi } from "./api"
import type { LoginRequest } from "@/types"
import toast from "react-hot-toast"
import { useAuthStore } from "@/store/authStore"
import { useNavigate } from "react-router-dom"
import { APP_ROUTES } from "@/constants"

export function useLogin() {
  const setAuth = useAuthStore((state) => state.setAuth)
  const navigate = useNavigate()

  return useMutation({
    mutationFn: (credentials: LoginRequest) => loginApi(credentials),
    onSuccess: (result) => {
      setAuth(result.data.user)
      toast.success("Welcome back!")
      navigate(APP_ROUTES.DASHBOARD)
    },
    onError: (error) => {
      console.log(error)
      toast.error(error.message)
    },
  })
}
