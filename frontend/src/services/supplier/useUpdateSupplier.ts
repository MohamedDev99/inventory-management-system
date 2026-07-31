import { useMutation, useQueryClient } from "@tanstack/react-query"
import { supplierKeys } from "./keys"
import { updateSupplier as updateSupplierApi } from "./api"
import type { UpdateSupplierRequest } from "@/lib/schemas/supplier/request"
import { toast } from "sonner"

export function useUpdateSupplier() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateSupplierRequest }) =>
      updateSupplierApi(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: supplierKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: supplierKeys.lists() })
      toast.success("Supplier updated successfully")
    },
    onError: () => {
      toast.error("Failed to update supplier")
    },
  })
}
