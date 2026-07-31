import { useMutation } from "@tanstack/react-query"
import { forgotPassword as forgotPasswordApi } from "./api"

export function useForgotPassword() {
  return useMutation({
    mutationFn: (email: string) => forgotPasswordApi(email),
  })
}
