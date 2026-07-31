import { useMutation, useQueryClient } from "@tanstack/react-query"
import { productKeys } from "./keys"
import { updateProduct as updateProductApi } from "./api"
import type { ProductFormData } from "@/lib/schemas"
import { toast } from "sonner"

export function useUpdateProduct() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: ProductFormData }) =>
      updateProductApi(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: productKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: productKeys.lists() })
      toast.success("Product updated successfully")
    },
    onError: () => {
      toast.error("Failed to update product")
    },
  })
}
