import { useMutation, useQueryClient } from "@tanstack/react-query"
import { supplierKeys } from "./keys"
import { createSupplier as createSupplierApi } from "./api"
import type { CreateSupplierRequest } from "@/lib/schemas/supplier/request"
import { toast } from "sonner"

export function useCreateSupplier() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreateSupplierRequest) => createSupplierApi(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: supplierKeys.lists() })
      toast.success("Supplier added successfully")
    },
    onError: () => {
      toast.error("Failed to add supplier")
    },
  })
}
