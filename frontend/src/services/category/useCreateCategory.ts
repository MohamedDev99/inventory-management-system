import { useMutation, useQueryClient } from "@tanstack/react-query"
import { categoryKeys } from "./keys"
import { createCategory as createCategoryApi } from "./api"
import type { CategoryFormData } from "@/lib/schemas"
import { toast } from "sonner"

export function useCreateCategory() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CategoryFormData) => createCategoryApi(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: categoryKeys.lists() })
      toast.success("Category added successfully")
    },
    onError: () => {
      toast.error("Failed to add category")
    },
  })
}
