import { useMutation, useQueryClient } from "@tanstack/react-query"
import { roleKeys } from "./keys"
import { updateRole, type CreateRoleRequest } from "./api"

export function useUpdateRole() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: CreateRoleRequest }) =>
      updateRole(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: roleKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: roleKeys.lists() })
    },
  })
}
