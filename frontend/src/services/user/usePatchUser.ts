import { useMutation, useQueryClient } from "@tanstack/react-query"
import { userKeys } from "./keys"
import { patchUser, type UpdateUserRequest } from "./api"

export function usePatchUser() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<UpdateUserRequest> }) =>
      patchUser(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: userKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: userKeys.lists() })
      queryClient.invalidateQueries({ queryKey: userKeys.me() })
    },
  })
}
