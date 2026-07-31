import { useMutation, useQueryClient } from "@tanstack/react-query"
import { supplierKeys } from "./keys"
import { deleteSupplier as deleteSupplierApi } from "./api"
import { toast } from "sonner"

export function useDeleteSupplier() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => deleteSupplierApi(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: supplierKeys.lists() })
      toast.success("Supplier deleted successfully")
    },
    onError: () => {
      toast.error("Failed to delete supplier")
    },
  })
}
