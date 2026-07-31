import { useQuery } from "@tanstack/react-query"
import { categoryKeys } from "./keys"
import { getCategories } from "./api"
import type { PageParams } from "@/types"

export function useCategories(params: PageParams = {}) {
  return useQuery({
    queryKey: categoryKeys.list(params),
    queryFn: () => getCategories(params),
  })
}
