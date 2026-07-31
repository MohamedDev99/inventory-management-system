import { useMutation, useQueryClient } from "@tanstack/react-query"
import { warehouseKeys } from "./keys"
import { deleteWarehouse as deleteWarehouseApi } from "./api"
import { toast } from "sonner"

export function useDeleteWarehouse() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => deleteWarehouseApi(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: warehouseKeys.lists() })
      toast.success("Warehouse deleted successfully")
    },
    onError: () => {
      toast.error("Failed to delete warehouse")
    },
  })
}
