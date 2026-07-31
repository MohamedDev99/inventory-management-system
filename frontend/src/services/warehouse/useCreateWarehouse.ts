import { useMutation, useQueryClient } from "@tanstack/react-query"
import { warehouseKeys } from "./keys"
import { createWarehouse as createWarehouseApi } from "./api"
import type { WarehouseFormData } from "@/lib/schemas"
import { toast } from "sonner"

export function useCreateWarehouse() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: WarehouseFormData) => createWarehouseApi(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: warehouseKeys.lists() })
      toast.success("Warehouse added successfully")
    },
    onError: () => {
      toast.error("Failed to add warehouse")
    },
  })
}
