import { useMutation, useQueryClient } from "@tanstack/react-query"
import { logout as logoutApi } from "./api"
import { authKeys } from "./keys"

export function useLogout() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: () => logoutApi(),
    onSuccess: () => {
      queryClient.removeQueries({ queryKey: authKeys.currentUser() })
    },
  })
}
