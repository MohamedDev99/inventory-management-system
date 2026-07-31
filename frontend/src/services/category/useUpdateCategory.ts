import { useMutation, useQueryClient } from "@tanstack/react-query"
import { categoryKeys } from "./keys"
import { updateCategory as updateCategoryApi } from "./api"
import type { CategoryFormData } from "@/lib/schemas"
import { toast } from "sonner"

export function useUpdateCategory() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: CategoryFormData }) =>
      updateCategoryApi(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: categoryKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: categoryKeys.lists() })
      toast.success("Category updated successfully")
    },
    onError: () => {
      toast.error("Failed to update category")
    },
  })
}
