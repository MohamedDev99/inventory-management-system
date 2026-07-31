import { useMutation, useQueryClient } from "@tanstack/react-query"
import { userKeys } from "./keys"
import { deleteUser } from "./api"

export function useDeleteUser() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => deleteUser(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: userKeys.lists() })
    },
  })
}
