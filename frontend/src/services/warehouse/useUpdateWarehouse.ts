import { useMutation, useQueryClient } from "@tanstack/react-query"
import { warehouseKeys } from "./keys"
import { updateWarehouse as updateWarehouseApi } from "./api"
import type { WarehouseFormData } from "@/lib/schemas"
import { toast } from "sonner"

export function useUpdateWarehouse() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: WarehouseFormData }) =>
      updateWarehouseApi(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: warehouseKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: warehouseKeys.lists() })
      toast.success("Warehouse updated successfully")
    },
    onError: () => {
      toast.error("Failed to update warehouse")
    },
  })
}
