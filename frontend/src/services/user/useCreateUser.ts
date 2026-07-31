import { useMutation, useQueryClient } from "@tanstack/react-query"
import { userKeys } from "./keys"
import { createUser, type CreateUserRequest } from "./api"

export function useCreateUser() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreateUserRequest) => createUser(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: userKeys.lists() })
    },
  })
}
