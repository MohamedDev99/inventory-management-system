import { useQuery } from "@tanstack/react-query"
import { stockKeys } from "./keys"
import { getInventoryByProduct } from "./api"

export function useInventoryByProduct(productId: number) {
  return useQuery({
    queryKey: stockKeys.byProduct(productId),
    queryFn: () => getInventoryByProduct(productId),
    enabled: !!productId,
  })
}
