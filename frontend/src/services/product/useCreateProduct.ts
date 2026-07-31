import { useMutation, useQueryClient } from "@tanstack/react-query"
import { productKeys } from "./keys"
import { createProduct as createProductApi } from "./api"
import type { ProductFormData } from "@/lib/schemas"
import { toast } from "sonner"

export function useCreateProduct() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: ProductFormData) => createProductApi(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: productKeys.lists() })
      toast.success("Product added successfully")
    },
    onError: () => {
      toast.error("Failed to add product")
    },
  })
}
