import { useQuery } from "@tanstack/react-query"
import { categoryKeys } from "./keys"
import { getCategory } from "./api"

export function useCategory(id: number) {
  return useQuery({
    queryKey: categoryKeys.detail(id),
    queryFn: () => getCategory(id),
    enabled: !!id,
  })
}
