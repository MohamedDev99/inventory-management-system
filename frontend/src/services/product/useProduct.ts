import { useQuery } from "@tanstack/react-query"
import { productKeys } from "./keys"
import { getProduct } from "./api"

export function useProduct(id: number) {
  return useQuery({
    queryKey: productKeys.detail(id),
    queryFn: () => getProduct(id),
    enabled: !!id,
  })
}
