import { useQuery } from "@tanstack/react-query"
import { categoryKeys } from "./keys"
import { getCategoryTree } from "./api"

export function useCategoryTree() {
  return useQuery({
    queryKey: categoryKeys.tree(),
    queryFn: () => getCategoryTree(),
  })
}
