import { useMutation, useQueryClient } from "@tanstack/react-query"
import { userKeys } from "./keys"
import { assignUserWarehouses, type AssignWarehousesRequest } from "./api"

export function useAssignUserWarehouses() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: AssignWarehousesRequest }) =>
      assignUserWarehouses(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: userKeys.warehouses(id) })
    },
  })
}
