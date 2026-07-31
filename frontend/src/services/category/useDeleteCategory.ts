import { useMutation, useQueryClient } from "@tanstack/react-query"
import { categoryKeys } from "./keys"
import { deleteCategory as deleteCategoryApi } from "./api"
import { toast } from "sonner"

export function useDeleteCategory() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => deleteCategoryApi(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: categoryKeys.lists() })
      toast.success("Category deleted successfully")
    },
    onError: () => {
      toast.error("Failed to delete category")
    },
  })
}
