import { useQuery } from "@tanstack/react-query"
import { productKeys } from "./keys"
import { getProducts } from "./api"
import type { PageParams } from "@/types"

export function useProducts(params: PageParams = {}) {
  return useQuery({
    queryKey: productKeys.list(params),
    queryFn: () => getProducts(params),
  })
}
