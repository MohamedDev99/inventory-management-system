import { useMutation, useQueryClient } from "@tanstack/react-query"
import { roleKeys } from "./keys"
import { deleteRole } from "./api"

export function useDeleteRole() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => deleteRole(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: roleKeys.lists() })
    },
  })
}
