import { useMutation } from "@tanstack/react-query"
import { resetPassword as resetPasswordApi } from "./api"

export function useResetPassword() {
  return useMutation({
    mutationFn: (data: { token: string; newPassword: string }) => 
      resetPasswordApi(data),
  })
}
