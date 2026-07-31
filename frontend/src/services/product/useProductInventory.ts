import { useQuery } from "@tanstack/react-query"
import { productKeys } from "./keys"
import { getProductInventory } from "./api"

export function useProductInventory(productId: number) {
  return useQuery({
    queryKey: productKeys.inventory(productId),
    queryFn: () => getProductInventory(productId),
    enabled: !!productId,
  })
}
