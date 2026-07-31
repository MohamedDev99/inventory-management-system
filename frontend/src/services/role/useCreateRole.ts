import { useMutation, useQueryClient } from "@tanstack/react-query"
import { roleKeys } from "./keys"
import { createRole, type CreateRoleRequest } from "./api"

export function useCreateRole() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreateRoleRequest) => createRole(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: roleKeys.lists() })
    },
  })
}
