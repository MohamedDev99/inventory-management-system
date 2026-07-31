import { useMutation, useQueryClient } from "@tanstack/react-query"
import { productKeys } from "./keys"
import { deleteProduct as deleteProductApi } from "./api"
import { toast } from "sonner"

export function useDeleteProduct() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => deleteProductApi(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: productKeys.lists() })
      toast.success("Product deleted successfully")
    },
    onError: () => {
      toast.error("Failed to delete product")
    },
  })
}
