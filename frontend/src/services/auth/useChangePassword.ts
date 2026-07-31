import { useMutation } from "@tanstack/react-query"
import { changePassword as changePasswordApi } from "./api"
import { toast } from "sonner"

export function useChangePassword() {
  return useMutation({
    mutationFn: (data: { currentPassword: string; newPassword: string }) => 
      changePasswordApi(data),
    onSuccess: () => {
      toast.success("Password updated successfully")
    },
    onError: () => {
      toast.error("Failed to update password")
    },
  })
}
